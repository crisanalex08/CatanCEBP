package com.example.gameserver.entity;




import com.example.gameserver.api.dto.User.PlayerDetailsDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(unique = true)
    @NotNull
    @NotEmpty
    private String name;
    private Long gameId;

    private boolean host;

    @NotNull
    public PlayerDetailsDTO toGetPlayerDTO() {
        PlayerDetailsDTO dto = new PlayerDetailsDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setHost(host);
        return dto;
    }
}
