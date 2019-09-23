package com.tz.web;

import com.tz.lock.CacheKeyGenerator;
import com.tz.lock.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class,args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
