package com.sentinel.service;

import com.sentinel.api.dto.monitoringgroup.*;
import com.sentinel.domain.*;
import com.sentinel.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MonitoringGroupService {

        private static final List<AlertStatus> ACTIVE_STATUSES = List.of(
                        AlertStatus.OPEN,
                        AlertStatus.ACKNOWLEDGED);

        private final MonitoringGroupRepository groups;
        private final MonitoringGroupMemberRepository members;
        private final ServerRepository servers;
        private final IncidentRepository incidents;
        private final MonitoringReportRepository reports;
        private final MonitoringReportItemRepository reportItems;

        public MonitoringGroupService(
                        MonitoringGroupRepository groups,
                        MonitoringGroupMemberRepository members,
                        ServerRepository servers,
                        IncidentRepository incidents,
                        MonitoringReportRepository reports,
                        MonitoringReportItemRepository reportItems) {
                this.groups = groups;
                this.members = members;
                this.servers = servers;
                this.incidents = incidents;
                this.reports = reports;
                this.reportItems = reportItems;
        }

        @Transactional
        public MonitoringGroupCreatedResponse createGroup(
                        CreateMonitoringGroupRequest request) {
                UUID ownerSessionId = UUID.randomUUID();

                MonitoringGroup group = groups.save(
                                new MonitoringGroup(request.name().trim(), ownerSessionId));

                MonitoringReport report = reports.save(
                                new MonitoringReport(group));

                return new MonitoringGroupCreatedResponse(
                                group.getId(),
                                group.getName(),
                                group.getOwnerSessionId(),
                                report.getId());
        }

        @Transactional
        public MonitoringGroupMemberCreatedResponse addServer(
                        UUID groupId,
                        UUID ownerSessionId,
                        AddMonitoringServerRequest request) {
                MonitoringGroup group = getOwnedGroup(groupId, ownerSessionId);

                ServerNode server = servers.findById(request.serverId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Server not found"));

                if (members.existsByGroup_IdAndServer_Id(groupId, server.getId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Server is already a member of this monitoring group");
                }

                MonitoringGroupMember member = members.save(
                                new MonitoringGroupMember(group, server));

                return new MonitoringGroupMemberCreatedResponse(
                                member.getSessionId(),
                                server.getId(),
                                server.getName(),
                                member.isSubmitted());
        }

        @Transactional(readOnly = true)
        public MonitoringGroupSessionsResponse getSessions(
                        UUID groupId,
                        UUID ownerSessionId) {
                getOwnedGroup(groupId, ownerSessionId);

                return buildSessionsResponse(groupId);
        }

        @Transactional
        public MonitoringGroupSessionsResponse removeMemberSession(
                        UUID groupId,
                        UUID sessionId,
                        UUID ownerSessionId) {
                getOwnedGroup(groupId, ownerSessionId);

                MonitoringGroupMember member = members.findById(sessionId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Monitoring session not found"));

                if (!member.getGroup().getId().equals(groupId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Monitoring session does not belong to this group");
                }

                members.delete(member);

                return buildSessionsResponse(groupId);
        }

        @Transactional
        public void submitMemberSession(
                        UUID groupId,
                        UUID sessionId) {
                groups.findById(groupId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Monitoring group not found"));

                MonitoringGroupMember member = members.findByGroup_IdAndSessionId(groupId, sessionId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Monitoring session not found in this group"));

                if (member.isSubmitted()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Monitoring session has already been submitted");
                }

                member.submit();
        }

        @Transactional(readOnly = true)
        public OwnerSessionResponse getOwnerSession(
                        UUID groupId,
                        UUID ownerSessionId) {
                MonitoringGroup group = getOwnedGroup(groupId, ownerSessionId);

                return new OwnerSessionResponse(group.getOwnerSessionId());
        }

        @Transactional
        public MonitoringReportSubmittedResponse submitReport(
                        UUID groupId,
                        UUID ownerSessionId,
                        SubmitMonitoringGroupRequest request) {
                getOwnedGroup(groupId, ownerSessionId);

                MonitoringReport report = reports.findById(request.reportId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Monitoring report not found"));

                if (!report.getGroup().getId().equals(groupId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Monitoring report does not belong to this group");
                }

                if (report.getStatus() != MonitoringReportStatus.DRAFT) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Monitoring report has already been submitted");
                }

                List<MonitoringGroupMember> submittedMembers = members.findAllByGroup_Id(groupId)
                                .stream()
                                .filter(MonitoringGroupMember::isSubmitted)
                                .toList();

                if (submittedMembers.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "There are no submitted monitoring sessions");
                }

                Map<IncidentKey, Integer> aggregated = new LinkedHashMap<>();

                for (MonitoringGroupMember member : submittedMembers) {
                        List<Incident> activeIncidents = incidents.findAllByServer_IdAndStatusIn(
                                        member.getServer().getId(),
                                        ACTIVE_STATUSES);

                        for (Incident incident : activeIncidents) {
                                IncidentKey key = new IncidentKey(
                                                incident.getTitle(),
                                                incident.getSeverity());

                                aggregated.merge(key, 1, Integer::sum);
                        }
                }

                if (aggregated.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Submitted sessions contain no active incidents");
                }

                List<MonitoringReportItem> items = new ArrayList<>();
                List<ConsolidatedMonitoringItemResponse> responseItems = new ArrayList<>();

                for (Map.Entry<IncidentKey, Integer> entry : aggregated.entrySet()) {
                        IncidentKey key = entry.getKey();
                        int quantity = entry.getValue();

                        items.add(new MonitoringReportItem(
                                        report,
                                        key.incidentType(),
                                        key.severity(),
                                        quantity));

                        responseItems.add(new ConsolidatedMonitoringItemResponse(
                                        key.incidentType(),
                                        key.severity(),
                                        quantity));
                }

                reportItems.saveAll(items);
                report.submit();

                int total = responseItems.stream()
                                .mapToInt(ConsolidatedMonitoringItemResponse::quantity)
                                .sum();

                return new MonitoringReportSubmittedResponse(
                                report.getId(),
                                report.getStatus(),
                                responseItems,
                                total);
        }

        private MonitoringGroupSessionsResponse buildSessionsResponse(
                        UUID groupId) {
                List<MonitoringParticipantResponse> participants = members.findAllByGroup_Id(groupId)
                                .stream()
                                .map(this::toParticipantResponse)
                                .toList();

                int total = participants.stream()
                                .mapToInt(MonitoringParticipantResponse::total)
                                .sum();

                return new MonitoringGroupSessionsResponse(participants, total);
        }

        private MonitoringParticipantResponse toParticipantResponse(
                        MonitoringGroupMember member) {
                ServerNode server = member.getServer();

                List<MonitoringItemResponse> items = incidents.findAllByServer_IdAndStatusIn(
                                server.getId(),
                                ACTIVE_STATUSES)
                                .stream()
                                .map(incident -> new MonitoringItemResponse(
                                                incident.getId(),
                                                incident.getTitle(),
                                                incident.getSeverity(),
                                                1))
                                .toList();

                int participantTotal = items.stream()
                                .mapToInt(MonitoringItemResponse::quantity)
                                .sum();

                return new MonitoringParticipantResponse(
                                member.getSessionId(),
                                "Server",
                                server.getName(),
                                items,
                                participantTotal,
                                member.isSubmitted());
        }

        private MonitoringGroup getOwnedGroup(
                        UUID groupId,
                        UUID ownerSessionId) {
                MonitoringGroup group = groups.findById(groupId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Monitoring group not found"));

                if (ownerSessionId == null ||
                                !group.getOwnerSessionId().equals(ownerSessionId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Only the group owner can perform this operation");
                }

                return group;
        }

        private record IncidentKey(
                        String incidentType,
                        AlertSeverity severity) {
        }
}