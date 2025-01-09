package com.example.gameserver.api.dto;


import lombok.Data;


@Data
public class GetMyTradesRequest {
    private Long gameId;
    private Long PlayerId;
}
