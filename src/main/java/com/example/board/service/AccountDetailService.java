package com.example.board.service;

import com.example.board.dto.AccountUserDetails;
import com.example.board.entity.AccountEntity;
import com.example.board.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("not exist id"));

        return new AccountUserDetails(accountEntity);
    }
}