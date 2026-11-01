package com.sentinel.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "monitoring_group_members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_monitoring_group_server",
            columnNames = {"group_id", "server_id"}
        )
    }
)
public class MonitoringGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private MonitoringGroup group;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerNode server;

    @Column(nullable = false)
    private boolean submitted = false;

    protected MonitoringGroupMember() {
    }

    public MonitoringGroupMember(MonitoringGroup group, ServerNode server) {
        this.group = group;
        this.server = server;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public MonitoringGroup getGroup() {
        return group;
    }

    public ServerNode getServer() {
        return server;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void submit() {
        this.submitted = true;
    }
}
