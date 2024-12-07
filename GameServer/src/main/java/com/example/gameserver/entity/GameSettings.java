package com.example.gameserver.entity;

import lombok.Data;

@Data
public class GameSettings {
    private int maxPlayers;
    private int currentPlayersCount;
}
