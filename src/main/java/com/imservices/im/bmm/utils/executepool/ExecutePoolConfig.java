package com.imservices.im.bmm.utils.executepool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutePoolConfig {

    /**
     * 核心线程数
     */
    @Value("${taskThreadPool.corePoolSize}")
    private int corePoolSize;

    /**
     * 最大线程数
     */
    @Value("${taskThreadPool.maxPoolSize}")
    private int maxPoolSize;

    /**
     * 线程活跃时间
     */
    @Value("${taskThreadPool.keepAliveSeconds}")
    private int keepAliveSeconds;

    /**
     * 队列容量
     */
    @Value("${taskThreadPool.queueCapacity}")
    private int queueCapacity;

    /**
     * 线程池，如果有特殊的业务场景，可以自行再添加线程池
     * 使用示例   在需要使用线程池的方法上增加注解 @Async("taskExecutor")
     * @return
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列容量
        executor.setQueueCapacity(queueCapacity);
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //线程名字前缀
        executor.setThreadNamePrefix("taskExecutor-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行,即变为单线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
