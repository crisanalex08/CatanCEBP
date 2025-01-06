package com.example.gameserver.repository;

import com.example.gameserver.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByGameIdAndTradeId(Long gameId, Long tradeId);

//    Optional<Trade> findByGameIdAndFromPlayerId(Long gameId, Long fromPlayerId);
//
//    Optional<Trade> findByGameIdAndToPlayerId(Long gameId, Long toPlayerId);
}