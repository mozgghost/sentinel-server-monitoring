package com.sentinel.api;

import com.sentinel.api.dto.monitoringgroup.*;
import com.sentinel.service.MonitoringGroupService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring-groups")
public class MonitoringGroupController {

    private final MonitoringGroupService service;

    public MonitoringGroupController(MonitoringGroupService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonitoringGroupCreatedResponse createGroup(
            @Valid @RequestBody CreateMonitoringGroupRequest request) {
        return service.createGroup(request);
    }

    @PostMapping("/{groupId}/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public MonitoringGroupMemberCreatedResponse addServer(
            @PathVariable("groupId") UUID groupId,
            @RequestHeader("X-Owner-Session-Id") UUID ownerSessionId,
            @Valid @RequestBody AddMonitoringServerRequest request) {
        return service.addServer(groupId, ownerSessionId, request);
    }

    @GetMapping("/{groupId}/sessions")
    public MonitoringGroupSessionsResponse getSessions(
            @PathVariable("groupId") UUID groupId,
            @RequestHeader("X-Owner-Session-Id") UUID ownerSessionId) {
        return service.getSessions(groupId, ownerSessionId);
    }

    @DeleteMapping("/{groupId}/sessions/{sessionId}")
    public MonitoringGroupSessionsResponse removeMemberSession(
            @PathVariable("groupId") UUID groupId,
            @PathVariable("sessionId") UUID sessionId,
            @RequestHeader("X-Owner-Session-Id") UUID ownerSessionId) {
        return service.removeMemberSession(
                groupId,
                sessionId,
                ownerSessionId);
    }

    @PostMapping("/{groupId}/sessions/{sessionId}/submit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitMemberSession(
            @PathVariable("groupId") UUID groupId,
            @PathVariable("sessionId") UUID sessionId) {
        service.submitMemberSession(groupId, sessionId);
    }

    @GetMapping("/{groupId}/owner-session")
    public OwnerSessionResponse getOwnerSession(
            @PathVariable("groupId") UUID groupId,
            @RequestHeader("X-Owner-Session-Id") UUID ownerSessionId) {
        return service.getOwnerSession(groupId, ownerSessionId);
    }

    @PostMapping("/{groupId}/submit")
    public MonitoringReportSubmittedResponse submitReport(
            @PathVariable("groupId") UUID groupId,
            @RequestHeader("X-Owner-Session-Id") UUID ownerSessionId,
            @Valid @RequestBody SubmitMonitoringGroupRequest request) {
        return service.submitReport(groupId, ownerSessionId, request);
    }
}