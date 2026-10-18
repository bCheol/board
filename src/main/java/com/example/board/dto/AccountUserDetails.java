package com.example.board.dto;

import com.example.board.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AccountUserDetails implements UserDetails {

    private final AccountEntity accountEntity;
    private int withdrawCheck = 0;

    public AccountUserDetails(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + accountEntity.getRole()));
    }

    @Override
    public String getPassword() {
        return accountEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return accountEntity.getAccountId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountEntity.getBlock() == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountEntity.getExist() == 1;
    }

    public String getRole() {
        return accountEntity.getRole();
    }

    public String getNickName() {
        return accountEntity.getNickName();
    }

    public String getHsName() {
        return accountEntity.getHsName();
    }

    public String getHsPhone() {
        return accountEntity.getHsPhone();
    }

    public void setNickName(String nickName) {
        accountEntity.setNickName(nickName);
    }

    public void setHsName(String name) {
        accountEntity.setHsName(name);
    }

    public void setHsPhone(String phone) {
        accountEntity.setHsPhone(phone);
    }

    public int getWithdrawCheck() {
        return withdrawCheck;
    }

    public void setWithdrawCheck(int withdrawCheck) {
        this.withdrawCheck = withdrawCheck;
    }
}