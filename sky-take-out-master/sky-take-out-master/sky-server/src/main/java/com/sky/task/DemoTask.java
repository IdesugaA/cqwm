package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description DemoTask
 * @Author songyu
 * @Date 2023-10-06
 */
@Slf4j
@Component //加入spring容器
public class DemoTask {

    //目标：每隔5秒执行一次这个方法
    @Scheduled(cron = "0/5 * * * * ?")
    public void demo(){
        log.info("hello world {}", LocalDateTime.now());
    }
}
