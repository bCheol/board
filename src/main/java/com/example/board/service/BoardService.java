package com.example.board.service;

import com.example.board.dto.BoardSearchCondition;
import com.example.board.dto.PageInfo;
import com.example.board.dto.ResponseListResult;
import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.BoardSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
    public class BoardService {

    private final BoardRepository boardRepository;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public ResponseListResult<BoardEntity> list(BoardSearchCondition cond) {

        if (cond.isCache()
                && ("latest".equals(cond.getSort())) && (cond.getKeyword() == null || cond.getKeyword().isBlank())
                && (1 <= cond.getPage() && cond.getPage() <= 3)) {

            Cache cache = cacheManager.getCache("ltBoardList");
            List<BoardEntity> value = cache.get("data", List.class);
            Long totalCount = cache.get("totalCount", Long.class);

            if (value != null && totalCount != null) {
                int start = (cond.getPage() - 1) * cond.getSize();

                PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), totalCount);
                List<BoardEntity> viewList = value.stream().skip(start).limit(cond.getSize()).toList();

                return new ResponseListResult<BoardEntity>(viewList, pageInfo);
            }
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

    public void cacheViewCount(Integer value) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("ltBoardList");

        cache.getNativeCache().asMap().compute("data", (data, old) -> {
            List<BoardEntity> list = (List<BoardEntity>) old;
            for (BoardEntity board : list) {
                if (board.getNum().equals(value)) {
                    board.setViewCount(board.getViewCount() + 1);
                    break;
                }
            }
            return list;
        });
    }

    public void cacheModifyAndDelete(String type, Object value) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("ltBoardList");

        if ("modify".equals(type)) {
            Map<String, Object> map = (Map<String, Object>) value;

            cache.getNativeCache().asMap().compute("data", (data, old) -> {
                List<BoardEntity> list = (List<BoardEntity>) old;
                for (BoardEntity board : list) {
                    if (board.getNum().equals(map.get("boardNum"))) {
                        board.setTitle((String) map.get("title"));
                        break;
                    }
                }
                return list;
            });
        } else if ("delete".equals(type)) {
            List<BoardEntity> data = (List<BoardEntity>) cache.getNativeCache().getIfPresent("data");
            boolean result = data.stream().anyMatch(board -> Objects.equals(value, board.getNum()));
            if (result)
                this.cacheWriteAndUpdate("update", null);;
        }
    }

    public void cacheWriteAndUpdate(String type, Object value) {

        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("ltBoardList");

        if ("write".equals(type)) {
            Map<String, Object> map = (Map<String, Object>) value;
            BoardEntity boardEntity = new BoardEntity();
            boardEntity.setNum((Integer) map.get("boardNum"));
            boardEntity.setTitle((String) map.get("title"));
            boardEntity.setTempWriter((String) map.get("nickName"));
            boardEntity.setCreateTm(LocalDateTime.now());
            boardEntity.setViewCount(0);

            final boolean[] exists = new boolean[1];

            cache.getNativeCache().asMap().compute("data", (data, oldList) -> {
                List<BoardEntity> list = (List<BoardEntity>) oldList;
                exists[0] = list.stream().anyMatch(board -> boardEntity.getNum().equals(board.getNum()));
                if (!exists[0])
                    list.add(0, boardEntity);

                return list;
            });

            if (!exists[0]) {
                cache.getNativeCache().asMap().compute("totalCount", (totalCount, oldCount) ->
                        (Long) oldCount + 1
                );
            }
        } else if ("update".equals(type)) {

            List<BoardEntity> list;
            Long totalCount;
            try {
                list = boardRepository.findTop45ByBlockNotOrderByCreateTmDescNumDesc(1);
                totalCount = boardRepository.countByBlockNot(1);
            } catch (Exception e) {
                log.error("cacheWriteAndUpdate() update", e);
                return;
            }
            cache.getNativeCache().asMap().compute("data", (data, oldList) -> new CopyOnWriteArrayList<>(list));
            cache.getNativeCache().asMap().compute("totalCount", (count, oldCount) -> totalCount);
        }
    }
}