package com.example.gameserver.services;

import org.springframework.stereotype.Service;

import com.example.gameserver.api.dto.GameCreateRequest;
import com.example.gameserver.api.dto.PlayerJoinRequest;
import com.example.gameserver.entity.Game;

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
