package com.busanbank.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BnkApplication {
    public static void main(String[] args) {
        SpringApplication.run(BnkApplication.class, args);
    }
}
