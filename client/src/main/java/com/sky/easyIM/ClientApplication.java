package com.sky.easyIM;

import com.sky.easyIM.client.WebSocketClient;
import org.springframework.boot.CommandLineRunner;

import java.net.URI;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class ClientApplication implements CommandLineRunner {

    WebSocketClient client;
    public static void main( String[] args ) {
        System.out.println( "Hello Client!" );
    }

    @Override
    public void run(String... args) throws Exception {
        this.connect();
    }

    private void connect(){
        URI uri = URI.create("");
        this.client = new WebSocketClient(uri);
        this.client.connect();
    }

    private String readCommand(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
