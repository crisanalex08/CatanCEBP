package com.example.gameserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.enums.GameStatus;


import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Long hostId;

    @OneToMany(mappedBy = "gameId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<User> players;


    @Enumerated(EnumType.STRING)
    private GameStatus status;
   

    @Embedded
    private GameSettings settings;

    public PlayerDetailsDTO getPlayerById(Long playerId) {
        User user = players.stream().filter(player -> player.getId().equals(playerId)).findFirst().orElse(null);
        if (user == null) {
            return null;
        }
        PlayerDetailsDTO player =  new PlayerDetailsDTO(){{
            setId(user.getId());
            setName(user.getName());
        }};

        return player;
    }
    
  

}
