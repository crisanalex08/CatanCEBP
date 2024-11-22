package com.example.gameserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gameserver.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
}