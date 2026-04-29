package com.unibuc.fmi.dass.sarighioleanu.authx.security;

import com.unibuc.fmi.dass.sarighioleanu.authx.AuditLogService;
import com.unibuc.fmi.dass.sarighioleanu.authx.auth.UserService;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditAction;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditResource;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditStatus;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditLogService auditLogService;
    private final UserService userService;

    @Autowired
    public CustomAuthenticationSuccessHandler(
            AuditLogService auditLogService,
            UserService userService
    ) {
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        User user = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails principal) {
            String email = principal.getUsername();
            user = userService.loadUserByEmail(email);
        }

        auditLogService.logAuth(
                user,
                AuditAction.LOGIN,
                AuditStatus.SUCCESS,
                AuditResource.AUTH,
                request.getRemoteAddr()
        );

        response.sendRedirect("/dashboard");
    }
}
