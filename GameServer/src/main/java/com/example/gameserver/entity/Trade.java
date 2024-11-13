package com.example.gameserver.entity;

import java.sql.Date;

import com.example.gameserver.enums.TradeStatus;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

@Data
public class Trade {
    @Id
    private String id;
    
    private Player seller;
    private Resources offering;
    private Resources requesting;
    private TradeStatus status;
    private Date created;
    private Date expires;
}
