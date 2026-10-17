package com.example.board.service;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final CacheManager cacheManager;
    private final BoardRepository boardRepository;
    private ScheduledFuture<?> scheduledTask;
    private final ReentrantLock reentrantLock = new ReentrantLock();

    public void startScheduler() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = taskScheduler.scheduleAtFixedRate(this::cacheUpdate, Duration.ofSeconds(60));
        }
    }

    public void stopScheduler() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            cacheManager.getCache("ltBoardList").clear();
        }
    }

    public boolean runningScheduler() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            return false;
        }
        return true;
    }

    public void cacheUpdate() {

        if (reentrantLock.tryLock()) {
            try {
                Cache cache = cacheManager.getCache("ltBoardList");

                Specification<BoardEntity> spec = Specification.where(BoardSpecification.notBlocked());
                for (int i = 1; i < 4; i++) {
                    Pageable pageable = PageRequest.of(i - 1, 15, Sort.by(Sort.Direction.DESC, "createTm", "num"));
                    Page<BoardEntity> page = boardRepository.findAll(spec, pageable);
                    PageInfo pageInfo = new PageInfo(i, 15, page.getTotalElements());
                    cache.put(i, new ResponseListResult<BoardEntity>(page.getContent(), pageInfo));
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

}