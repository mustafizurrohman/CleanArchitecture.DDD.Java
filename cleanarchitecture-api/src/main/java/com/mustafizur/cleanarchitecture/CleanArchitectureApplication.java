package com.mustafizur.cleanarchitecture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CleanArchitectureApplication {
    private static final Logger log = LoggerFactory.getLogger(CleanArchitectureApplication.class);

    public static void main(String[] args) {
        var started = System.nanoTime();
        SpringApplication.run(CleanArchitectureApplication.class, args);
        var durationMs = (System.nanoTime() - started) / 1_000_000;
        log.info("API startup completed durationMs={}", durationMs);
    }
}
