package com.sentinel.repository;

import com.sentinel.domain.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRuleRepository extends JpaRepository<AlertRule, UUID> {

    List<AlertRule> findAllByOrderByCreatedAtDesc();

    boolean existsByNameIgnoreCase(String name);

    List<AlertRule> findAllByEnabledTrue();
}