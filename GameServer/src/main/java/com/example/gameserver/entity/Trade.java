package com.example.gameserver.entity;

import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.enums.TradeStatus;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tradeId;
    private Long gameId;
    private Long fromPlayerId;
    private Long toPlayerId;
    @Enumerated(EnumType.STRING)
    private ResourceType offer;
    @Enumerated(EnumType.STRING)
    private ResourceType request;
    @Enumerated(EnumType.STRING)
    private TradeStatus status;
    //    private Date created;
    //    private Date expires;

    public Trade(Long gameId, Long fromPlayerId, Long toPlayerId, ResourceType offer, ResourceType request, TradeStatus tradeStatus) {
        this.gameId = gameId;
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
        this.offer = offer;
        this.request = request;
        this.status = tradeStatus;
    }

    public Trade() {

    }
}
