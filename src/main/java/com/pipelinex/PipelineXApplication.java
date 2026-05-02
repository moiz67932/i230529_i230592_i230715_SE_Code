package com.pipelinex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class PipelineXApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipelineXApplication.class, args);
    }
}
