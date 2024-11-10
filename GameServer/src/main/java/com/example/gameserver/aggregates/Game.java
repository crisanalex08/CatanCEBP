package com.example.gameserver.aggregates;

import lombok.Data;
import com.example.gameserver.enums.GameStatus;
import java.util.List;

@Data
public class Game {
    private String id;
    private String hostName;
    private List<Player> players;
    private GameStatus status;
    private GameSettings settings;
}
