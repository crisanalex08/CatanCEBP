package com.example.gameserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gameserver.entity.User;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
    User getUserByname(String username);
}
