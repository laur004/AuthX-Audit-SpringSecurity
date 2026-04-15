package com.unibuc.fmi.dass.sarighioleanu.authx.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String resource;

    @Column(name = "resource_id", nullable = true)
    private String resourceId;

    @CreationTimestamp
    private OffsetDateTime timestamp;

    @Column(name = "ip_address",nullable = false)
    private String ipAddress;

    public AuditLog() {}

    public AuditLog(String id, User user, String action, String resource, String resourceId, OffsetDateTime timestamp, String ipAddress) {
        this.id = id;
        this.user = user;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public String getResourceId() {
        return resourceId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
