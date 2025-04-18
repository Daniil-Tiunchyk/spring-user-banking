package com.example.spring_user_banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringUserBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringUserBankingApplication.class, args);
    }

}
