package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.*;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.AuditLogRepository;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private AuditLogRepository auditLogRepository;
    private CurrentUserProvider currentUserProvider;


    @Autowired
    public AuditLogService(
            AuditLogRepository auditLogRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    private void log(
            User user,
            AuditAction action,
            AuditStatus status,
            AuditResource resource,
            String resourceId,
            String ipAddress
    ) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action.name()+"_"+status.name());
        log.setResource(resource.name());
        log.setResourceId(resourceId);
        log.setTimestamp(OffsetDateTime.now());
        log.setIpAddress(ipAddress);

        auditLogRepository.save(log);
    }

    public void logAuth(
            User user,
            AuditAction action,
            AuditStatus status,
            AuditResource resource,
            String ipAddress
    ) {
        log(user, action, status, resource, null, ipAddress);
    }

    public void logAccountBlocked(String userId, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUser(null);
        log.setAction(AuditAction.ACCOUNT_BLOCKED.name());
        log.setResource(AuditResource.AUTH.name());
        log.setResourceId(userId);
        log.setTimestamp(OffsetDateTime.now());
        log.setIpAddress(ipAddress);

        auditLogRepository.save(log);
    }

    public void logTicket(
            AuditAction action,
            AuditStatus status,
            AuditResource resource,
            String resourceId,
            String ipAddress
    ) {
        User user = currentUserProvider.getCurrentUser();
        log(user, action, status, resource, resourceId, ipAddress);
    }

    public void logAuditAccess(AuditStatus status, String ipAddress) {
        User user = currentUserProvider.getCurrentUser();

        log(user, AuditAction.VIEW_LOGS, status, AuditResource.AUDIT, null, ipAddress);
    }

}
