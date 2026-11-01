package com.sentinel.api;

import com.sentinel.api.dto.CreateServerRequest;
import com.sentinel.api.dto.ServerResponse;
import com.sentinel.api.dto.UpdateServerRequest;
import com.sentinel.service.MonitoringService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final MonitoringService service;

    public ServerController(MonitoringService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServerResponse> list() {
        return service.listServers();
    }

    @PostMapping
    public ServerResponse create(
            @Valid @RequestBody CreateServerRequest request) {
        return service.createServer(request);
    }

    @PutMapping("/{id}")
    public ServerResponse update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateServerRequest request) {
        return service.updateServer(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        service.deleteServer(id);
    }

    @GetMapping("/{id}/metrics")
    public List<Map<String, Object>> history(
            @PathVariable("id") UUID id) {
        return service.history(id);
    }
}