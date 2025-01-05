package com.example.gameserver.api.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class GameMessage {
        private Long gameId;
        private String sender;
        private String content;
        private LocalDateTime timestamp;
}

