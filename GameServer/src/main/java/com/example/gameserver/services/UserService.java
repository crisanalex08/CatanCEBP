package com.example.gameserver.services;

import com.example.gameserver.entity.User;
import com.example.gameserver.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private  UsersRepository usersRepository;
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User retrieveOrCreateUser(String username) {
       var user = usersRepository.getUserByUsername(username);

         if(user == null) {
              user = new User();
              user.setName(username);
              usersRepository.save(user);
         }
            return user;
    }

    public User getUserByUsername(String username) {
        return usersRepository.getUserByUsername(username);
    }
}
