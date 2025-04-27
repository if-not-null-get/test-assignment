package com.ingemark.testassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TestAssignmentApplication {
    //since as of now there's not much configuration,
    //this is put just here instead of a dedicated config class
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(TestAssignmentApplication.class, args);
    }

}
