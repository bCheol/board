package com.example.board.service;

import lombok.RequiredArgsConstructor;
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
    private final BoardService boardService;
    private ScheduledFuture<?> scheduledTask;

    public void startScheduler() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = taskScheduler.scheduleAtFixedRate(()->boardService.cacheWriteAndUpdate("update", null), Duration.ofSeconds(60));
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

}