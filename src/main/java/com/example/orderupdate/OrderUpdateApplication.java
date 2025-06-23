package com.example.orderupdate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderUpdateApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderUpdateApplication.class, args);
    }
}
