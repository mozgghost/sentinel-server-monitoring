package com.sentinel.api.dto;
import com.sentinel.domain.*; import java.time.Instant; import java.util.UUID;
public record ServerResponse(UUID id,String name,String host,String environment,ServerStatus status,Double cpuPercent,Double memoryPercent,Instant lastHeartbeat) { public static ServerResponse from(ServerNode s){return new ServerResponse(s.getId(),s.getName(),s.getHost(),s.getEnvironment(),s.getStatus(),s.getCpuPercent(),s.getMemoryPercent(),s.getLastHeartbeat());}}
