package com.example.gameserver.services;

import com.example.gameserver.aggregates.Game;
import com.example.gameserver.aggregates.GameCreateRequest;
import com.example.gameserver.aggregates.PlayerJoinRequest;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    public Game createGame(GameCreateRequest request) {
        return null;
    }

    public Game startGame(String gameId) {
        return null;
    }

    public Game getGameById(String gameId) {
        return null;
    }

    public Game joinGame(String gameId, PlayerJoinRequest request) {
        return null;
    }
}
