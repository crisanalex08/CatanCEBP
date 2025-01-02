package com.example.gameserver.repository;

import com.example.gameserver.entity.Building;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByGameIdAndPlayerId(Long gameId, Long playerId);
}
