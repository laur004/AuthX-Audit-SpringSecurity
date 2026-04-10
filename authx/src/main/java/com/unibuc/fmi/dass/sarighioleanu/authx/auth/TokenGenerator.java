package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Component
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String getToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
