package com.sentinel.api;

import com.sentinel.api.dto.*;
import com.sentinel.service.MonitoringService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    private final MonitoringService service;

    public ServerController(MonitoringService s) {
        service = s;
    }

    @GetMapping
    public List<ServerResponse> list() {
        return service.listServers();
    }

    @PostMapping
    public ServerResponse create(@Valid @RequestBody CreateServerRequest r) {
        return service.createServer(r);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteServer(id);
    }

    @GetMapping("/{id}/metrics")
    public List<Map<String, Object>> history(@PathVariable UUID id) {
        return service.history(id);
    }
}
