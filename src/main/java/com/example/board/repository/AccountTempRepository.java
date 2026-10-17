package com.example.board.repository;

import com.example.board.entity.AccountTempEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTempRepository extends JpaRepository<AccountTempEntity, String> {

}