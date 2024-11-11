package com.example.gameserver.aggregates;

import java.sql.Date;

import com.example.gameserver.enums.TradeStatus;

import lombok.Data;

@Data
public class Trade {
    private String id;
    private Player seller;
    private Resources offering;
    private Resources requesting;
    private TradeStatus status;
    private Date created;
    private Date expires;
}
