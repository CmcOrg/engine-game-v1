package com.cmcorg.engine.game.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GameStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameStartApplication.class, args);
    }

}
