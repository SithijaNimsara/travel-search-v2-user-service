package com.example.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);

    public static void main(String[] args) {
        logger.info("UserServiceApplication --------- v2 -------- ");
        SpringApplication.run(UserServiceApplication.class, args);
        logger.info("UserServiceApplication --------- v2 -------- ");
    }

}
