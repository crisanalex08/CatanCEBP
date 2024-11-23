package com.example.gameserver.repository;

import com.example.gameserver.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resources, Long> {
    Optional<Resources> findByGameIdAndPlayerId(Long gameId, Long playerId);
}