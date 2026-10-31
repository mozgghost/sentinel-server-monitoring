package com.sentinel.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="metrics", indexes=@Index(name="idx_metric_server_time",columnList="server_id, recordedAt"))
public class Metric {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="server_id") private ServerNode server;
 @Column(nullable=false) private double cpuPercent; @Column(nullable=false) private double memoryPercent; @Column(nullable=false) private Instant recordedAt;
 protected Metric() {} public Metric(ServerNode server,double cpu,double memory,Instant at){this.server=server;this.cpuPercent=cpu;this.memoryPercent=memory;this.recordedAt=at;}
 public UUID getId(){return id;} public double getCpuPercent(){return cpuPercent;} public double getMemoryPercent(){return memoryPercent;} public Instant getRecordedAt(){return recordedAt;}
}
