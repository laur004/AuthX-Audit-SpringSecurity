package com.unibuc.fmi.dass.sarighioleanu.authx.security;

import com.unibuc.fmi.dass.sarighioleanu.authx.AuditLogService;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditAction;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditResource;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final AuditLogService auditLogService;

    @Autowired
    public CustomAuthenticationFailureHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        auditLogService.logAuth(
                null,
                AuditAction.LOGIN,
                AuditStatus.FAILURE,
                AuditResource.AUTH,
                request.getRemoteAddr()
        );

        if (exception instanceof LockedException) {
            response.sendRedirect("/login?locked");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
