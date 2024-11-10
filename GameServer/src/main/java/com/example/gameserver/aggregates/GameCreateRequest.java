package com.example.gameserver.aggregates;

import lombok.Data;

@Data
public class GameCreateRequest {
    private String hostName;
    private GameSettings settings;
}