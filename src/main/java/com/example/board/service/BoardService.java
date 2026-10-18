package com.example.board.service;

import com.example.board.dto.BoardSearchCondition;
import com.example.board.dto.PageInfo;
import com.example.board.dto.ResponseListResult;
import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.BoardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
    public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ltBoardList", key = "#cond.page", condition = "#cond.cache and (#cond.sort.equals('latest') and #cond.searchType.equals('all') and (#cond.keyword == null or #cond.keyword.isBlank()) and (1 <= #cond.page and #cond.page <= 3))")
    public ResponseListResult<BoardEntity> list(BoardSearchCondition cond) {
        Specification<BoardEntity> spec = Specification.where(BoardSpecification.notBlocked());
        if (StringUtils.hasText(cond.getKeyword())) {
            spec = spec.and(BoardSpecification.keywordLike(cond.getSearchType(), cond.getKeyword()));
        }

        Pageable pageable = PageRequest.of(cond.getPage() - 1, cond.getSize(), resolveSort(cond.getSort()));
        Page<BoardEntity> page = boardRepository.findAll(spec, pageable);

        PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), page.getTotalElements());
        return new ResponseListResult<BoardEntity>(page.getContent(), pageInfo);
    }


    private Sort resolveSort(String sort) {
        if ("view".equals(sort)) {
            return Sort.by(Sort.Direction.DESC, "viewCount", "num");
        }
        return Sort.by(Sort.Direction.DESC, "createTm", "num");
    }

    public BoardEntity getById(int boardNum) {
        return boardRepository.findById(boardNum).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void increaseViewCount(int boardNum) {
        boardRepository.increaseViewCount(boardNum);
    }

    @Transactional(rollbackFor = Exception.class)
    public void write(BoardEntity board) {
        boardRepository.save(board);
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(int boardNum, String accountId, String title, String body) {
        BoardEntity boardEntity = boardRepository.findById(boardNum).orElse(null);
        if (boardEntity == null)
            return 0;
        if (!equalsAccountId(boardEntity, accountId))
            return -1;

        boardEntity.setTitle(title);
        boardEntity.setBody(body);
        boardEntity.setUpdateTm(LocalDateTime.now());
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(int boardNum, String accountId) {
        BoardEntity boardEntity = boardRepository.findById(boardNum).orElse(null);
        if (boardEntity == null)
            return 0;
        if (boardEntity.getBlock() == 1)
            return -1;
        if (!equalsAccountId(boardEntity, accountId))
            return -2;

        boardRepository.delete(boardEntity);
        return 1;
    }

    private boolean equalsAccountId(BoardEntity boardEntity, String accountId) {
        return boardEntity.getAccountId().equals(accountId);
    }
}