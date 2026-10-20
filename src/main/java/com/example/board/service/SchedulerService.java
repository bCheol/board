package com.example.board.service;

import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final CacheManager cacheManager;
    private final BoardRepository boardRepository;
    private ScheduledFuture<?> scheduledTask;

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

        Cache cache = cacheManager.getCache("ltBoardList");
        cache.put("data", boardRepository.findTop45ByBlockNotOrderByCreateTmDescNumDesc(1));
        cache.put("totalCount", boardRepository.count());
    }

}