package com.example.board.service;

import com.example.board.dto.BoardSearchCondition;
import com.example.board.dto.PageInfo;
import com.example.board.dto.ResponseListResult;
import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.BoardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
    public class BoardService {

    private final BoardRepository boardRepository;
    private final CacheManager cacheManager;
    private final SchedulerService schedulerService;

    @Transactional(readOnly = true)
    public ResponseListResult<BoardEntity> list(BoardSearchCondition cond) {

        if (cond.isCache()
                && ("latest".equals(cond.getSort())) && (cond.getKeyword() == null || cond.getKeyword().isBlank())
                && (1 <= cond.getPage() && cond.getPage() <= 3)) {

                Cache cache = cacheManager.getCache("ltBoardList");
                List<BoardEntity> value = cache.get("data", List.class);
                long totalCount = cache.get("totalCount", Long.class);
                int start = (cond.getPage()-1) * 15;
                int end = start + 15;

                PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), totalCount);
                return new ResponseListResult<BoardEntity>(value.subList(start, end), pageInfo);
        }

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
    public int write(String title, String body, String writer) {

        BoardEntity boardEntity = new BoardEntity(title, body, writer);
        boardRepository.save(boardEntity);
        return boardEntity.getNum();
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

    public void cacheModify(String type, int boardNum, String title, String nickName) {

        Cache cache = cacheManager.getCache("ltBoardList");
        List<BoardEntity> value = cache.get("data", List.class);

        if (value == null)
            return;

        if ("viewCount".equals(type)) {
            value.stream().filter(board -> boardNum == board.getNum()).forEach(board -> board.setViewCount(board.getViewCount() + 1));
        } else if ("write".equals(type)) {
            BoardEntity boardEntity = new BoardEntity();
            boardEntity.setNum(boardNum);
            boardEntity.setTitle(title);
            boardEntity.setTempWriter(nickName);
            boardEntity.setCreateTm(LocalDateTime.now());
            boardEntity.setViewCount(1);
            value.add(0, boardEntity);
        } else if ("modify".equals(type)) {
            value.stream().filter(board -> boardNum == board.getNum()).forEach(board -> board.setTitle(title));
        } else if ("delete".equals(type)) {
            boolean result = value.stream().anyMatch(board -> boardNum == board.getNum());
            if (result)
                schedulerService.cacheUpdate();
        }
    }
}