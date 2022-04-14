package com.sky.easyIM;

import com.sky.easyIM.server.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@Slf4j
@SpringBootApplication
public class ServerApplication implements CommandLineRunner {
    public static void main( String[] args ) {
        new SpringApplication(ServerApplication.class).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("springboot 服务启动");
        WebSocketServer sever = new WebSocketServer("/chat");
        sever.start((short) 8081);
    }
}
