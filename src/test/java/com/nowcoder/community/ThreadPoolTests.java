package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    // JDK 线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK 可定时执行任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring 普通的线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //Spring 可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(long m) throws InterruptedException {
        Thread.sleep(m);
    }

    // JDK 线程池使用
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };

        for(int i=0;i<10;i++){
            executorService.submit(task);
        }
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // JDK 定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);
        try {
            sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Spring 普通线程池

    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskExecutor");
            }
        };

        for(int i=0;i<10;i++){
            taskExecutor.submit(task);
        }

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Spring 定时任务线程池
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskScheduler");
            }
        };

        taskScheduler.scheduleAtFixedRate(task,new Date(System.currentTimeMillis()+10000),10000);

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async // Spring 普通线程池的（简化）
    public void exec(){
        logger.debug("adkaasdkj");
    }

//    @Scheduled(initialDelay = 10000,fixedRate = 1000) // Spring 定时线程池的（简化）
    public void exec2(){
        logger.debug("adkaasdkj");
    }

    // Spring 普通线程池的（简化）
    @Test
    public void aaa(){
        for(int i=0;i<10;i++){
            exec();
        }
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
