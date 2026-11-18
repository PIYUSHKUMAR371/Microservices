package com.jindalsaw.it.backend_gateway.security;



import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class HmacSecurityUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    // Secret key shared across internal Jindal Saw server instances
    private static final String SECRET_KEY = "JindalSaw_IT_Secure_Key_2026";

    public boolean verifySignature(String payload, String incomingSignature) {
        if (incomingSignature == null || incomingSignature.isBlank()) {
            return false;
        }
        String computedSignature = calculateHmac(payload);
        return computedSignature.equalsIgnoreCase(incomingSignature);
    }

    public String calculateHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculating HMAC SHA-256 signature", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
