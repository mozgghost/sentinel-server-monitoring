package com.sentinel.repository;

import com.sentinel.domain.AlertStatus;
import com.sentinel.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    List<Incident> findAllByOrderByCreatedAtDesc();

    boolean existsByServer_IdAndTitleAndStatusIn(
            UUID serverId,
            String title,
            Collection<AlertStatus> statuses);

    List<Incident> findAllByServer_IdAndTitleAndStatusIn(
            UUID serverId,
            String title,
            Collection<AlertStatus> statuses);

    List<Incident> findAllByServer_IdAndStatusIn(
            UUID serverId,
            Collection<AlertStatus> statuses);
}