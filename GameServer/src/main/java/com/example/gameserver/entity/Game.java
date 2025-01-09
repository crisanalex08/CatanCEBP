package com.example.gameserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.gameserver.api.dto.User.PlayerDetailsDTO;
import com.example.gameserver.enums.GameStatus;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;


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
    private String name;
    private Long hostId;

    // @ManyToMany(fetch = FetchType.EAGER)
    // private Set<User> players;
    @OneToMany(mappedBy = "gameId", fetch = FetchType.EAGER)
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
