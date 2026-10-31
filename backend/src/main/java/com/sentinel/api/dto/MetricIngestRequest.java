package com.sentinel.api.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
public record MetricIngestRequest(@NotNull java.util.UUID serverId,@DecimalMin("0.0") @DecimalMax("100.0") double cpuPercent,@DecimalMin("0.0") @DecimalMax("100.0") double memoryPercent,@NotNull Instant recordedAt) {}
