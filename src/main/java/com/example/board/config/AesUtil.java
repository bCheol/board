package com.example.board.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;


@Slf4j
@Component
public class AesUtil {

    String ALGORITHM = "AES/GCM/NoPadding";
    int TAG_LENGTH_BITS = 128;
    int IV_LENGTH_BYTES = 12;
    @Value("${AES_KEY}")
    String KEY;
    SecureRandom secureRandom = new SecureRandom();

    public String encrypt(String plainText) {

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] encryptedText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedText.length);
            buffer.put(iv).put(encryptedText);

            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            log.error("encrypt", e);
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipherText) {

        try {
            byte[] data = Base64.getDecoder().decode(cipherText);
            byte[] iv = Arrays.copyOfRange(data, 0, IV_LENGTH_BYTES);
            byte[] encryptedText = Arrays.copyOfRange(data, IV_LENGTH_BYTES, data.length);

            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] decryptedText = cipher.doFinal(encryptedText);
            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decrypt", e);
            throw new RuntimeException(e);
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

    public String createHashValue(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String previousDecrypt(String previousKey, String cipherText) {

        try {
            byte[] data = Base64.getDecoder().decode(cipherText);
            byte[] iv = Arrays.copyOfRange(data, 0, IV_LENGTH_BYTES);
            byte[] encrypted = Arrays.copyOfRange(data, IV_LENGTH_BYTES, data.length);

            SecretKeySpec keySpec = new SecretKeySpec(previousKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("previousDecrypt", e);
            throw new RuntimeException(e);
        }
    }

}