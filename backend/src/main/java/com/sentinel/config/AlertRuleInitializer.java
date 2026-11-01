package com.sentinel.config;

import com.sentinel.domain.AlertRule;
import com.sentinel.domain.AlertSeverity;
import com.sentinel.repository.AlertRuleRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AlertRuleInitializer implements CommandLineRunner {

    private final AlertRuleRepository repository;

    public AlertRuleInitializer(AlertRuleRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<AlertRule> defaults = List.of(
                new AlertRule(
                        "High CPU usage",
                        "Critical CPU utilization threshold",
                        "CPU",
                        ">=",
                        90,
                        AlertSeverity.CRITICAL
                ),
                new AlertRule(
                        "Elevated CPU usage",
                        "Warning CPU utilization threshold",
                        "CPU",
                        ">=",
                        75,
                        AlertSeverity.WARNING
                ),
                new AlertRule(
                        "High memory usage",
                        "Critical memory utilization threshold",
                        "MEMORY",
                        ">=",
                        92,
                        AlertSeverity.CRITICAL
                ),
                new AlertRule(
                        "Elevated memory usage",
                        "Warning memory utilization threshold",
                        "MEMORY",
                        ">=",
                        80,
                        AlertSeverity.WARNING
                )
        );

        repository.saveAll(defaults);
    }
}