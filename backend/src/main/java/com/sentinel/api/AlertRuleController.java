package com.sentinel.api;

import com.sentinel.api.dto.AlertRuleResponse;
import com.sentinel.api.dto.CreateAlertRuleRequest;
import com.sentinel.api.dto.UpdateAlertRuleEnabledRequest;
import com.sentinel.service.AlertRuleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alert-rules")
public class AlertRuleController {

    private final AlertRuleService service;

    public AlertRuleController(AlertRuleService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlertRuleResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertRuleResponse create(
            @Valid @RequestBody CreateAlertRuleRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}/enabled")
    public AlertRuleResponse setEnabled(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAlertRuleEnabledRequest request) {
        return service.setEnabled(id, request.enabled());
    }
}