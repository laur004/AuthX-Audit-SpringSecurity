package com.unibuc.fmi.dass.sarighioleanu.authx.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String emailError = "";
        String passwordError = "";

        if(exception instanceof UsernameNotFoundException) {
            emailError = "Email not found";
        } else if(exception instanceof BadCredentialsException){
            passwordError = "Invalid password";
        } else {
            emailError = "Authentication Failed";
        }

        String redirectUrl = "/login?emailError=" +
                URLEncoder.encode(emailError, StandardCharsets.UTF_8) +
                "&passwordError=" +
                URLEncoder.encode(passwordError, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);

    }
}
