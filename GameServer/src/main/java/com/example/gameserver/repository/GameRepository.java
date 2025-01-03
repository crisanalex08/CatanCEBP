package com.example.gameserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gameserver.entity.Game;
import com.example.gameserver.enums.GameStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    Game findGameById(Long id);

    List<Game> findByStatus(GameStatus status);

}
