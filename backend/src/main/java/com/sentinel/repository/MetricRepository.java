package com.sentinel.repository;

import com.sentinel.domain.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MetricRepository extends JpaRepository<Metric, UUID> {
    List<Metric> findTop100ByServer_IdOrderByRecordedAtDesc(UUID serverId);
}
