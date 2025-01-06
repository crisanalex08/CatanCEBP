package com.example.gameserver.api.dto.User;

import lombok.Data;

@Data
public class PlayerDetailsDTO {

    private Long id;
    private String name;
    private boolean host;
}
