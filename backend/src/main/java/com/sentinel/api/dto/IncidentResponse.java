package com.sentinel.api.dto;
import com.sentinel.domain.*; import java.time.Instant; import java.util.UUID;
public record IncidentResponse(UUID id,UUID serverId,String serverName,AlertSeverity severity,AlertStatus status,String title,String description,Instant createdAt,Instant acknowledgedAt) { public static IncidentResponse from(Incident i){return new IncidentResponse(i.getId(),i.getServer().getId(),i.getServer().getName(),i.getSeverity(),i.getStatus(),i.getTitle(),i.getDescription(),i.getCreatedAt(),i.getAcknowledgedAt());}}
