package com.sentinel.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="incidents")
public class Incident {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(optional=false) @JoinColumn(name="server_id") private ServerNode server;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AlertSeverity severity;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AlertStatus status=AlertStatus.OPEN;
 @Column(nullable=false) private String title; @Column(length=1000) private String description;
 @Column(nullable=false) private Instant createdAt; private Instant acknowledgedAt; private Instant resolvedAt;
 protected Incident() {} public Incident(ServerNode s,AlertSeverity sev,String title,String desc){server=s;severity=sev;this.title=title;description=desc;createdAt=Instant.now();}
 public UUID getId(){return id;} public ServerNode getServer(){return server;} public AlertSeverity getSeverity(){return severity;} public AlertStatus getStatus(){return status;} public String getTitle(){return title;} public String getDescription(){return description;} public Instant getCreatedAt(){return createdAt;} public Instant getAcknowledgedAt(){return acknowledgedAt;}
 public void acknowledge(){if(status==AlertStatus.OPEN){status=AlertStatus.ACKNOWLEDGED;acknowledgedAt=Instant.now();}}
 public void resolve(){status=AlertStatus.RESOLVED;resolvedAt=Instant.now();}
}
