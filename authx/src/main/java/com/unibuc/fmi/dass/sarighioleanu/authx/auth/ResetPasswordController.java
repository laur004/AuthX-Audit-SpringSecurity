package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.AuditLogService;
import com.unibuc.fmi.dass.sarighioleanu.authx.dto.ForgotPasswordRequest;
import com.unibuc.fmi.dass.sarighioleanu.authx.dto.ResetPasswordRequest;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public ResetPasswordController(
            ResetPasswordService resetPasswordService,
            UserService userService,
            AuditLogService auditLogService
    ) {
        this.resetPasswordService = resetPasswordService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(
            @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request
    ) {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest httpServletRequest
    ) {

        User user = userService.loadUserByEmail(request.getEmail());

        if (bindingResult.hasErrors()) {

            auditLogService.logAuth(
                    user,
                    AuditAction.GENERATE_RESET_PASSWORD_TOKEN,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );

            return "forgot-password";
        }

        resetPasswordService.requestPasswordReset(request.getEmail());
        model.addAttribute("message", "A reset link has been sent to your email address.");

        auditLogService.logAuth(
                user,
                AuditAction.GENERATE_RESET_PASSWORD_TOKEN,
                AuditStatus.SUCCESS,
                AuditResource.AUTH,
                httpServletRequest.getRemoteAddr()
        );

        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model
    ) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        model.addAttribute("resetPasswordRequest", request);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest
    ) {
        User user = resetPasswordService.findUserByToken(request.getToken());

        if (bindingResult.hasErrors()) {
            auditLogService.logAuth(
                    user,
                    AuditAction.CHANGE_PASSWORD,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );
            return "reset-password";
        }

        try {
            resetPasswordService.resetPassword(request.getToken(), request.getNewPassword());

            auditLogService.logAuth(
                    user,
                    AuditAction.CHANGE_PASSWORD,
                    AuditStatus.SUCCESS,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );

        } catch (PasswordUnacceptableException e){
            bindingResult.rejectValue("newPassword", "newPassword.unacceptable", e.getMessage());
            auditLogService.logAuth(
                    user,
                    AuditAction.CHANGE_PASSWORD,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );
            return "reset-password";
        } catch (ResetPasswordTokenExpiredException e) {
            bindingResult.reject("token.expired", e.getMessage());
            auditLogService.logAuth(
                    user,
                    AuditAction.CHANGE_PASSWORD,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );
            return "reset-password";
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("token", e.getMessage());
            auditLogService.logAuth(
                    user,
                    AuditAction.CHANGE_PASSWORD,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    httpServletRequest.getRemoteAddr()
            );
            return "reset-password";
        }

        return "redirect:/login?resetSuccess";
    }

}
