package com.sentinel.service;

import com.sentinel.api.dto.AlertRuleResponse;
import com.sentinel.api.dto.CreateAlertRuleRequest;
import com.sentinel.domain.AlertRule;
import com.sentinel.domain.AlertSeverity;
import com.sentinel.repository.AlertRuleRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AlertRuleService {

    private final AlertRuleRepository repository;

    public AlertRuleService(AlertRuleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AlertRuleResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AlertRuleResponse::from)
                .toList();
    }

    @Transactional
    public AlertRuleResponse create(CreateAlertRuleRequest request) {
        String normalizedName = request.name().trim();

        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An alert rule with this name already exists");
        }

        AlertRule rule = new AlertRule(
                normalizedName,
                request.description() == null
                        ? ""
                        : request.description().trim(),
                request.metric(),
                request.operator(),
                request.threshold(),
                AlertSeverity.valueOf(request.severity())
        );

        try {
            return AlertRuleResponse.from(repository.save(rule));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An alert rule with this name already exists");
        }
    }

    @Transactional
    public AlertRuleResponse setEnabled(UUID id, boolean enabled) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alert rule not found"));

        rule.setEnabled(enabled);

        return AlertRuleResponse.from(repository.save(rule));
    }
}