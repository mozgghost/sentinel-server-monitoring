package com.sentinel.repository;

import com.sentinel.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

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
}