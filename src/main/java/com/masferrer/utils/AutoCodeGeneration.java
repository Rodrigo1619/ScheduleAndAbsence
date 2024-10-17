package com.masferrer.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class AutoCodeGeneration {
    
    public String generateVerificationCode(){
        Random random = new Random();
        int verificationCode = 10000000 + random.nextInt(90000000);
        return String.valueOf(verificationCode);
    }
}
