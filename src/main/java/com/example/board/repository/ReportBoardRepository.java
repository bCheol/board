package com.example.board.repository;

import com.example.board.entity.ReportBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportBoardRepository extends JpaRepository<ReportBoardEntity, Integer> {

    Page<ReportBoardEntity> findByResultIsNull(Pageable pageable);
    Page<ReportBoardEntity> findByResultIsNotNull(Pageable pageable);
    ReportBoardEntity findByBoardNumAndFromId(Integer boardNum, String fromId);
    List<ReportBoardEntity> findByBoardNum(int boardNum);
}