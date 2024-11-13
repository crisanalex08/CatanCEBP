package com.example.gameserver.entity;

import lombok.Data;

@Data
public class GameCreateRequest {
    private String hostName;
    private GameSettings settings;
}