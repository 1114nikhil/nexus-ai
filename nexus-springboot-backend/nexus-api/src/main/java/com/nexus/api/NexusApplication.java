package com.nexus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nexus")
public class NexusApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class,args);
    }
}
