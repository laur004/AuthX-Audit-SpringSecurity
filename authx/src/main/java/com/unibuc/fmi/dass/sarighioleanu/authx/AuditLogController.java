package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditLog;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditStatus;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/audit-logs")
    public String list(
            Model model,
            HttpServletRequest request
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getAuthorities());

        List<AuditLog> auditLogs = auditLogService.findAll();
        model.addAttribute("auditLogs", auditLogs);

        auditLogService.logAuditAccess(AuditStatus.SUCCESS, request.getRemoteAddr());

        return "audit-logs";
    }


}
