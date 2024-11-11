package com.example.gameserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gameserver.aggregates.Resources;
import com.example.gameserver.aggregates.Trade;
import com.example.gameserver.services.TradeService;

import io.swagger.models.Response;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/games/{gameId}/trades/")
@Tag(name = "Trade Controller", description = "Operations to manage trades")
public class TradeController {

    private final TradeService tradeService;

    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/{playerId}/create-trade")
    public ResponseEntity<Response> createTrade(@PathVariable String gameId, @PathVariable String playerId, @RequestBody Resources offer, @RequestBody Resources request) {

        Trade trade = tradeService.createTrade(gameId, playerId, offer, request);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
        
    }
    @GetMapping()
    public ResponseEntity<Response> getTrades(@PathVariable String gameId) {
        Trade trade = tradeService.getTrades(gameId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<Response> getTrade(@PathVariable String gameId, @PathVariable String tradeId) {
        Trade trade = tradeService.getTrade(gameId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping("/{playerId}/accept-trade/{tradeId}")
    public ResponseEntity<Response> acceptTrade(@PathVariable String gameId,@PathVariable String tradeId, @PathVariable String playerId) {
        Trade trade = tradeService.acceptTrade(gameId, playerId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{playerId}/decline-trade/{tradeId}")   
    public ResponseEntity<Response> declineTrade(@PathVariable String gameId,@PathVariable String tradeId, @PathVariable String playerId) {
        Trade trade = tradeService.declineTrade(gameId, playerId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Response>(HttpStatus.OK);
    }






    
}
