package com.example.gameserver.api.dto;

import com.example.gameserver.entity.GameSettings;

import lombok.Data;

@Data
public class GameCreateRequest {
    private String name;
    private String hostname;
    private GameSettings settings;
}