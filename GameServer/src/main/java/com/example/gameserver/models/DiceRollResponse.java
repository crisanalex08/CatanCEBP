package com.example.gameserver.models;

import lombok.Data;

@Data
public class DiceRollResponse {
    private int diceRoll;
    private Long gameId;
    private Long playerId;
    private String message;
    private boolean success;
    private String error;

    public DiceRollResponse(int diceRoll, Long gameId, Long playerId, String message, boolean success, String error) {
        this.diceRoll = diceRoll;
        this.gameId = gameId;
        this.playerId = playerId;
        this.message = message;
        this.success = success;
        this.error = error;
    }

}
