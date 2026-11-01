package com.sentinel.repository;

import com.sentinel.domain.MonitoringGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringGroupMemberRepository
        extends JpaRepository<MonitoringGroupMember, UUID> {

    List<MonitoringGroupMember> findAllByGroup_Id(UUID groupId);

    Optional<MonitoringGroupMember> findByGroup_IdAndServer_Id(
            UUID groupId,
            UUID serverId
    );

    Optional<MonitoringGroupMember> findByGroup_IdAndSessionId(
            UUID groupId,
            UUID sessionId
    );

    boolean existsByGroup_IdAndServer_Id(UUID groupId, UUID serverId);
}