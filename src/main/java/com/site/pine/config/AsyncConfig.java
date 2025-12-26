package com.site.pine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     * @EnableAsync
     *
     * 이 어노테이션이 있어야
     * @Async 메서드가 "비동기"로 동작한다.
     *
     * 없으면?
     * → @Async 붙여도 그냥 일반 메서드처럼 동기 실행됨
     */

    /**
     * Async 전용 스레드 풀
     *
     * 목적:
     * - 웹 요청 스레드(Tomcat)와 분리
     * - ffmpeg 같은 무거운 작업이
     *   웹 요청을 막지 않도록 하기 위함
     */
    @Bean
    public Executor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 개수
        executor.setCorePoolSize(2);

        // 최대 스레드 개수
        // (core 초과 시 생성)
        executor.setMaxPoolSize(4);

        // 대기 큐 크기
        // (동시에 몰릴 때 버퍼)
        executor.setQueueCapacity(20);

        // 스레드 이름 (로그 확인용)
        executor.setThreadNamePrefix("SHORTS-ASYNC-");

        // 애플리케이션 종료 시
        // 실행 중인 작업을 기다릴지 여부
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 종료 대기 시간 (초)
        executor.setAwaitTerminationSeconds(10);

        executor.initialize();
        return executor;
    }
}
