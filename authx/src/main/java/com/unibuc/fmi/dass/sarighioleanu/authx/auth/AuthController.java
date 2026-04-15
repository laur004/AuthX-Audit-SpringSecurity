package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.AuditLogService;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditAction;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditResource;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditStatus;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            User registeredUser = userService.registerUser(registerRequest.getEmail(), registerRequest.getPassword());

            auditLogService.logAuth(
                    registeredUser,
                    AuditAction.REGISTER,
                    AuditStatus.SUCCESS,
                    AuditResource.AUTH,
                    request.getRemoteAddr()
            );
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "email.exists", e.getMessage());

            auditLogService.logAuth(
                    null,
                    AuditAction.REGISTER,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    request.getRemoteAddr()
            );

            return "register";
        } catch (PasswordUnacceptableException e) {
            bindingResult.rejectValue("password", "password.unacceptable", e.getMessage());

            auditLogService.logAuth(
                    null,
                    AuditAction.REGISTER,
                    AuditStatus.FAILURE,
                    AuditResource.AUTH,
                    request.getRemoteAddr()
            );

            return "register";
        }

        return "redirect:/login?registered";
    }

}
