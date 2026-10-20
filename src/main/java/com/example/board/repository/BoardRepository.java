package com.example.board.repository;

import com.example.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer>, JpaSpecificationExecutor<BoardEntity> {

    @Modifying
    @Query("Update BoardEntity b Set b.viewCount = b.viewCount + 1 Where b.num = :boardNum")
    void increaseViewCount(int boardNum);

    List<BoardEntity> findTop45ByBlockNotOrderByCreateTmDescNumDesc(int block);
}