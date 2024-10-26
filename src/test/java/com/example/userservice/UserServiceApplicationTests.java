package com.example.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.config.enabled=false",
        "spring.datasource.url=jdbc:mysql://localhost:3306/travel",
        "spring.datasource.username=testuser",
        "spring.datasource.password=testpass"
})
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
