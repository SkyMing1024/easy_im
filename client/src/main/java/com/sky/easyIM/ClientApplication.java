package com.sky.easyIM;

import com.sky.easyIM.client.Client;
import com.sky.easyIM.client.WebSocketClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class ClientApplication  implements CommandLineRunner{

    WebSocketClient client;
    public static void main( String[] args ) {
        new SpringApplication(ClientApplication.class).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        this.connect();
    }

    private void connect(){
        URI uri = URI.create("ws://localhost:8081/chat");
        this.client = new WebSocketClient(uri);
        this.client.connect();
    }

    private String readCommand(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
