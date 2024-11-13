package com.example.gameserver.entity;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

import com.example.gameserver.enums.GameStatus;
import java.util.List;

@Data
public class Game {
    
    @Id
    private String id;
    
    private String hostName;
    private List<Player> players;
    private GameStatus status;
    private GameSettings settings;
}
