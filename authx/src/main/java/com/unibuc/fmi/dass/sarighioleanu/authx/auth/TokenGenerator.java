package com.unibuc.fmi.dass.sarighioleanu.authx.auth;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TokenGenerator {

    Random random = new Random(100);
    public String getToken() {
        return String.valueOf(random.nextInt(100_000, 999_999));
    }

}
