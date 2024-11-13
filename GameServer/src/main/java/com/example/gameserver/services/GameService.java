package com.example.gameserver.services;

import org.springframework.stereotype.Service;

import com.example.gameserver.entity.Game;
import com.example.gameserver.entity.GameCreateRequest;
import com.example.gameserver.entity.PlayerJoinRequest;

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
