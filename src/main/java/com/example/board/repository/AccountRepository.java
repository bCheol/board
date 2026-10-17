package com.example.board.repository;

import com.example.board.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String>, JpaSpecificationExecutor<AccountEntity> {

    AccountEntity findByAccountIdAndExist(String accountId, int exist);
    AccountEntity findByNameAndPhone(String name, String phone);
    AccountEntity findByAccountIdAndNameAndPhone(String accountId, String name, String phone);
    boolean existsByNickName(String nickName);
    boolean existsByNameAndExist(String name, int exist);
    boolean existsByPhoneAndExist(String phone, int exist);
    @Query("Select new com.example.board.entity.AccountEntity(a.accountId, a.name, a.phone) From AccountEntity a")
    List<AccountEntity> findAccountInfo();
}