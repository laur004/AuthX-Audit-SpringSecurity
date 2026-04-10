package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.repository.UserRepository;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CompromisedPasswordChecker compromisedPasswordChecker
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return  org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRole().name())
                .build();

    }

    @Transactional
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void registerUser(String email, String rawPassword) throws PasswordUnacceptableException {
//        if (userRepository.findByEmail(email.toLowerCase()).isPresent()) {
//            throw new EmailAlreadyExistsException("Email already in use");
//        }

        if (rawPassword == null || rawPassword.length() < 8 || !rawPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$")) {
            throw new PasswordUnacceptableException("Password should be at least 8 characters and contain at least upper, lower, digit and special character.");
        }

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(rawPassword);
        if (decision.isCompromised()) {
            throw new PasswordUnacceptableException("Choose a different password.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.USER);

        userRepository.save(user);
    }



}
