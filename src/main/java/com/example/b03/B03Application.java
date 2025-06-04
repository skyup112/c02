package com.example.b03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.b03.domain")
public class B03Application {
    public static void main(String[] args) {
        SpringApplication.run(B03Application.class, args);
    }
}