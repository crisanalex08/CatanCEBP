package com.example.gameserver.entity;

import com.example.gameserver.enums.PlayerStatus;

import jakarta.persistence.Id;
import lombok.Data;

@Data
class Player {
    @Id    
    private String id;
    
    private String name;
    private PlayerStatus status;
}