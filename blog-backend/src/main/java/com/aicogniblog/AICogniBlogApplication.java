package com.aicogniblog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@MapperScan("com.aicogniblog.**.mapper")
@EnableAsync
public class AICogniBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(AICogniBlogApplication.class, args);
        log.info("------------------启动成功--------------");
    }
}
