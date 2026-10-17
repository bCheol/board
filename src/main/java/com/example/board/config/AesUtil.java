package com.example.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Base64;


@Component
public class AesUtil {

    String ALGORITHM = "AES/ECB/PKCS5Padding";
    @Value("${AES_KEY}")
    String KEY;

    public String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encrypt Fail", e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decrypt Fail", e);
        }
    }

    public String nameInfo(String name) {
        String decryptName = decrypt(name);
        return decryptName.charAt(0) + "***";
    }

    public String phoneInfo(String phone) {
        String decryptPhone = decrypt(phone);
        return decryptPhone.substring(0, 2) + "*" + decryptPhone.substring(3, 5) + "**" + decryptPhone.substring(7, 9) + "**";
    }

    public String previousDecrypt(String previousKey, String cipherText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(previousKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (InvalidKeyException e) {
            return decrypt(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("previous Decrypt Fail", e);
        }
    }

}