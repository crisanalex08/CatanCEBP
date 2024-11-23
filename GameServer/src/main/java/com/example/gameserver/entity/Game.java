package com.example.gameserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.gameserver.enums.GameStatus;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long hostId;

 
    @Getter
    @ElementCollection
    private Set<Player> players;
    
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Embedded
    private GameSettings settings;

    public Player getPlayerById(Long playerId) {
        return players.stream().filter(player -> player.getId().equals(playerId)).findFirst().orElse(null);
    }

}
