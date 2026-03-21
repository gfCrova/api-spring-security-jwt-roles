package com.example.demogc;

import com.example.demogc.infrastructure.config.AdminBootstrapProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class DemoGcApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoGcApplication.class, args);
    }

}
