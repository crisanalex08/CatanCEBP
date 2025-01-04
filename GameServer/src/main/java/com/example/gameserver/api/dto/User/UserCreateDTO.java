package com.example.gameserver.api.dto.User;

import com.example.gameserver.entity.User;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String name;
    private String username;
    private String password;

    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setName(name);
     
        
        return user;
    }

}
