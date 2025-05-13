package com.bank.util;

import java.util.Random;

public class IdGeneratorUtil {
    private static final Random random = new Random();

    public static long generateUserId() {
        return 10000000L + random.nextInt(90000000); // Ensures it's 8 digits
    }
    
}