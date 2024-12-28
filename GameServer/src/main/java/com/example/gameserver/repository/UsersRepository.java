package com.example.gameserver.repository;

import java.util.HashSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gameserver.entity.User;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
    User getUserByName(String username);
    User getUserById(Long id);
    HashSet<User> findAllByGameId(Long gameId);
}
