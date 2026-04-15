package com.unibuc.fmi.dass.sarighioleanu.authx;

import com.unibuc.fmi.dass.sarighioleanu.authx.auth.UserService;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final UserService userService;

    public CurrentUserProvider(UserService userService) {
        this.userService = userService;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return authentication.getName();
    }

    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        if (email == null) {
            return null;
        }
        return userService.loadUserByEmail(email);
    }
}
