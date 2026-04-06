package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.dto.ForgotPasswordRequest;
import com.unibuc.fmi.dass.sarighioleanu.authx.dto.ResetPasswordRequest;
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

    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
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
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }

        resetPasswordService.requestPasswordReset(request.getEmail());
        model.addAttribute("message", "If the account exists, a reset link has been generated.");
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
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "reset-password";
        }

        try {
            resetPasswordService.resetPassword(request.getToken(), request.getNewPassword());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("token", e.getMessage());
            return "reset-password";
        }

        return "redirect:/login?resetSuccess";
    }

}
