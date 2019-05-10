package ru.eltex.magnus.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class App {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(App.class, args);
        System.setProperty("java.awt.headless", "false");
        StreamersServer.start();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (!"exit".equals(in.readLine().trim().toLowerCase())) ;
        }
        SpringApplication.exit(context);
    }
}