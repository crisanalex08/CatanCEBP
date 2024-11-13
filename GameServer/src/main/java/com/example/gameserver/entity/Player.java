package com.example.gameserver.entity;

import com.example.gameserver.enums.PlayerStatus;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

@Data
class Player {
    @Id
    private String id;
    
    private String name;
    private PlayerStatus status;
}