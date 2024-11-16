package com.example.gameserver.entity;

import lombok.Data;


import com.example.gameserver.enums.GameStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Data
@Entity
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String hostName;
    private List<Player> players;
    private GameStatus status;
    private GameSettings settings;
}
