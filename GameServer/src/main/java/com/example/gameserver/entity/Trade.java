package com.example.gameserver.entity;

import java.sql.Date;

import com.example.gameserver.enums.TradeStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Data
public class Trade {
    @Id
    private String id;
    private User offeringUser;
    private Resources offering;
    private Resources requesting;
    private TradeStatus status;
    private Date created;
    private Date expires;
}
