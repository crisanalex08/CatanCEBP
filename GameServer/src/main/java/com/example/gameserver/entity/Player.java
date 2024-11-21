package com.example.gameserver.entity;

import com.example.gameserver.enums.PlayerStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Embeddable
public class Player {
    private Long id;
    private String name;
    private PlayerStatus status;
}