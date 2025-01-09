package com.example.gameserver.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GamesWebSocketHandler(), "/games").setAllowedOrigins("*");
        registry.addHandler(new GamesWebSocketHandler(), "/lobby/{gameId}").setAllowedOrigins("*");
    }

    @Bean
    public GamesWebSocketHandler gamesWebSocketHandler() {
        return new GamesWebSocketHandler();
    }

  
}