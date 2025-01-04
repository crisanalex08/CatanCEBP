package com.example.gameserver.entity;

import com.example.gameserver.enums.TradeStatus;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long gameId;
    private Long fromPlayerId;
    private Long toPlayerId;
//    @OneToOne
//    private Resources offering;
//    @OneToOne
//    private Resources requesting;
    @Enumerated(EnumType.STRING)
    private TradeStatus status;
    //    private Date created;
    //    private Date expires;

    public Trade(Long gameId, Long fromPlayerId, Long toPlayerId/*, Resources offer, Resources request*/, TradeStatus tradeStatus) {
        this.gameId = gameId;
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
//        this.offering = offer;
//        this.requesting = request;
        this.status = tradeStatus;
    }

    public Trade() {

    }
}
