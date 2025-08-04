/**
 * User: Himal_J
 * Date: 2/10/2025
 * Time: 4:27 PM
 * <p>
 */

package com.billing.service.util;

import lombok.extern.log4j.Log4j2;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Log4j2
public class RandomGeneratorUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String getRandom6DigitNumber() {
        try {
            log.error("called random 6 digit generation number");
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            return String.format("%06d", number);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    public static String generateRandomToken() {
        byte[] randomBytes = new byte[10];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
