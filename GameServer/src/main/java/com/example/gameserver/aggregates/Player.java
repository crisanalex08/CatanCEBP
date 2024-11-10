package com.example.gameserver.aggregates;

import com.example.gameserver.enums.PlayerStatus;
import lombok.Data;

@Data
class Player {
    private String name;
    private PlayerStatus status;
}