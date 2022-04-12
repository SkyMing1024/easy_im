package com.sky.easyIM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class ServerApplication {
    public static void main( String[] args ) {
        new SpringApplication(ServerApplication.class).run(args);
    }
}
