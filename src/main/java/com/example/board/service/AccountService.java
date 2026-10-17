package com.example.board.service;

import com.example.board.config.AesUtil;
import com.example.board.entity.AccountEntity;
import com.example.board.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesUtil aesUtil;

    @Value("${TEMP_PW_KEY}")
    String TEMP_PASSWORD_CHARS;

    @Transactional(rollbackFor = Exception.class)
    public void register(String accountId, String password, String nickName, String name, String phone) {

        String encodePassword = passwordEncoder.encode(password);
        String encryptName = aesUtil.encrypt(name);
        String encryptPhone = aesUtil.encrypt(phone);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(accountId);
        accountEntity.setPassword(encodePassword);
        accountEntity.setNickName(nickName);
        accountEntity.setName(encryptName);
        accountEntity.setPhone(encryptPhone);
        accountRepository.save(accountEntity);
    }

    public boolean existId(String accountId) {
        return accountRepository.existsById(accountId);
    }

    public boolean existNickName(String nickName) {
        return accountRepository.existsByNickName(nickName);
    }

    public boolean existName(String name) {
        String encryptName = aesUtil.encrypt(name);
        return accountRepository.existsByNameAndExist(encryptName, 1);
    }

    public boolean existPhone(String phone) {
        String encryptPhone = aesUtil.encrypt(phone);
        return accountRepository.existsByPhoneAndExist(encryptPhone, 1);
    }

    public String findId(String name, String phone) {

        String encryptName = aesUtil.encrypt(name);
        String encryptPhone = aesUtil.encrypt(phone);
        AccountEntity accountEntity = accountRepository.findByNameAndPhone(encryptName, encryptPhone);
        if (accountEntity == null || accountEntity.getExist() == 0) {
            return "일치하는 아이디가 없습니다.";
        } else {
            if (accountEntity.getBlock() == 0) {
                return "회원님의 아이디는 " + accountEntity.getAccountId() + "입니다.";
            } else {
                return "차단된 계정입니다.";
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(String accountId, String name, String phone) {

        String encryptName = aesUtil.encrypt(name);
        String encryptPhone = aesUtil.encrypt(phone);
        AccountEntity accountEntity = accountRepository.findByAccountIdAndNameAndPhone(accountId, encryptName, encryptPhone);
        if (accountEntity == null || accountEntity.getExist() == 0) {
            return "일치하는 회원이 없습니다.";
        } else {
            if (accountEntity.getBlock() == 0) {
                String tempPassword = createTempPassword();
                String encodePassword = passwordEncoder.encode(tempPassword);
                accountEntity.setPassword(encodePassword);
                return "임시 비밀번호가 발급되었습니다:  " + tempPassword + " 로그인 후 반드시 비밀번호를 변경해주세요.";
            } else {
                return "차단된 계정입니다.";
            }
        }
    }

    private String createTempPassword() {

        StringBuilder sb = new StringBuilder(10);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(secureRandom.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    public AccountEntity getByAccountId(String accountId) {

        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(null);
        if (accountEntity != null) {
            accountEntity.setName(aesUtil.nameInfo(accountEntity.getName()));
            accountEntity.setPhone(aesUtil.phoneInfo(accountEntity.getPhone()));
        }
        return accountEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateInfo(String accountId, String field, String value) {

        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(null);
        if (accountEntity == null) {
            return 0;
        }
        switch (field) {
            case "password":
                String encodePassword = passwordEncoder.encode(value);
                accountEntity.setPassword(encodePassword);
                break;
            case "nickName":
                accountEntity.setNickName(value);
                break;
            case "name":
                String encryptName = aesUtil.encrypt(value);
                accountEntity.setName(encryptName);
                break;
            case "phone":
                String encryptPhone = aesUtil.encrypt(value);
                accountEntity.setPhone(encryptPhone);
                break;
        }
        return 1;
    }

    public boolean verifyPassword(String accountId, String rawPassword) {

        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(null);
        return accountEntity != null && passwordEncoder.matches(rawPassword, accountEntity.getPassword());
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdraw(String accountId) {

        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(null);
        if (accountEntity == null) {
            return;
        }
        accountEntity.setExist(0);
    }

}