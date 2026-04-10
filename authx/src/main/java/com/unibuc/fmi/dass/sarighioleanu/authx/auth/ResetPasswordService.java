package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import com.unibuc.fmi.dass.sarighioleanu.authx.repository.UserRepository;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.PasswordResetToken;
import com.unibuc.fmi.dass.sarighioleanu.authx.model.User;
import com.unibuc.fmi.dass.sarighioleanu.authx.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService {

    private final EmailService emailService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final CompromisedPasswordChecker compromisedPasswordChecker;


    @Autowired
    public ResetPasswordService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator,
            EmailService emailService,
            CompromisedPasswordChecker compromisedPasswordChecker
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }


    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        String token = tokenGenerator.getToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendResetPasswordEmail(
                user.getEmail(),
                resetToken.getToken()
        );
    }

    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (newPassword == null || newPassword.length() < 8 || !newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$")) {
            throw new PasswordUnacceptableException("Password should be at least 8 characters and contain at least upper, lower, digit and special character.");
        }

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(newPassword);
        if (decision.isCompromised()) {
            throw new PasswordUnacceptableException("Choose a different password.");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
