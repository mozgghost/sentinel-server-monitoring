package com.sentinel.repository;

import com.sentinel.domain.MonitoringGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MonitoringGroupRepository
        extends JpaRepository<MonitoringGroup, UUID> {
}
