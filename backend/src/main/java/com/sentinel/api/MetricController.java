package com.sentinel.api;

import com.sentinel.api.dto.MetricIngestRequest;
import com.sentinel.service.MonitoringService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricController {
    private final MonitoringService service;
    @Value("${sentinel.agent-token}")
    String token;

    public MetricController(MonitoringService s) {
        service = s;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingest(@RequestHeader(value = "X-Agent-Token", required = false) String supplied,
            @Valid @RequestBody MetricIngestRequest r) {
        if (supplied == null || !token.equals(supplied))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        service.ingest(r);
        return ResponseEntity.accepted().build();
    }
}
