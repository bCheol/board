package com.example.board.repository;

import com.example.board.entity.BoardEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

@NoArgsConstructor
public final class BoardSpecification {

    public static Specification<BoardEntity> notBlocked() {
        return (root, query, cb) -> cb.equal(root.get("block"), 0);
    }

    public static Specification<BoardEntity> keywordLike(String searchType, String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + EscapeCharacter.DEFAULT.escape(keyword) + "%";
            if ("title".equals(searchType)) {
                return cb.like(root.get("title"), pattern);
            }
            if ("body".equals(searchType)) {
                return cb.like(root.get("body"), pattern);
            }
            return cb.or(
                    cb.like(root.get("title"), pattern),
                    cb.like(root.get("body"), pattern)
            );
        };
    }
}