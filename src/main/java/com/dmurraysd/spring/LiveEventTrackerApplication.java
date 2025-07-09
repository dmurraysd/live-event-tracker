package com.dmurraysd.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class LiveEventTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveEventTrackerApplication.class, args);
    }

}
