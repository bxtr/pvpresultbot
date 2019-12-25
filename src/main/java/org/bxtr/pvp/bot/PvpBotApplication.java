package org.bxtr.pvp.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class PvpBotApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(PvpBotApplication.class, args);
    }
}
