package com.example.gameserver.entity;


import java.util.Set;

import com.example.gameserver.api.dto.User.PlayerDetailsDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "players")
    private Set<Game> games;

    public PlayerDetailsDTO toGetPlayerDTO() {
        PlayerDetailsDTO dto = new PlayerDetailsDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }

    
}
