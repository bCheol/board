package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService threadPool() {
        return new ThreadPoolExecutor(
                5,
                5,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3000)
        );
    }

}