package com.sentinel.api;

import com.sentinel.api.dto.IncidentResponse;
import com.sentinel.service.MonitoringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final MonitoringService service;

    public IncidentController(MonitoringService service) {
        this.service = service;
    }

    @GetMapping
    public List<IncidentResponse> list() {
        return service.listIncidents();
    }

    @PostMapping("/{id}/acknowledge")
    public IncidentResponse acknowledge(@PathVariable("id") UUID id) {
        return service.acknowledge(id);
    }
}