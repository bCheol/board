package com.example.board.repository;

import com.example.board.entity.AccountEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

public final class AdminAccountSpecification {

    private AdminAccountSpecification() {
    }

    public static Specification<AccountEntity> roleEquals(String role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<AccountEntity> roleLike(String role) {
        String pattern = "%" + role;
        return (root, query, cb) -> cb.like(root.get("role"), pattern);
    }

    public static Specification<AccountEntity> existEquals(Integer exist) {
        return (root, query, cb) -> cb.equal(root.get("exist"), exist);
    }

    public static Specification<AccountEntity> blockEquals(Integer block) {
        return (root, query, cb) -> cb.equal(root.get("block"), block);
    }

    public static Specification<AccountEntity> accountIdLike(String keyword) {
        return (root, query, cb) -> cb.like(root.get("accountId"), "%" + EscapeCharacter.DEFAULT.escape(keyword) + "%");
    }
}