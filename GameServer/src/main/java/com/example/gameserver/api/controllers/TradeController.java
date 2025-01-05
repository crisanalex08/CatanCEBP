package com.example.gameserver.api.controllers;

import java.util.List;

import com.example.gameserver.enums.TradeStatus;
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

import com.example.gameserver.api.dto.TradeCreateRequestDTO;
import com.example.gameserver.entity.Trade;
import com.example.gameserver.services.TradeService;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/games/trades/")
@Tag(name = "Trade Controller", description = "Operations to manage trades")
public class TradeController {

    private final TradeService tradeService;

    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Operation (summary = "Create a trade")
    @PostMapping("/merchant-trade")
    public ResponseEntity<TradeStatus> createTrade(@RequestBody TradeCreateRequestDTO request) {
        TradeStatus tradeStatus = tradeService.merchantTrade(request);
        if (tradeStatus == TradeStatus.CANCELLED) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tradeStatus, HttpStatus.CREATED);
        
    }

    @Operation (summary = "Get all trades")
    @GetMapping()
    public ResponseEntity<List<Trade>> getTrades(@PathVariable String gameId) {
        List<Trade> trades = tradeService.getTrades(gameId);
        if (trades == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trades, HttpStatus.OK);
    }


    @Operation (summary = "Get a trade")
    @GetMapping("/{tradeId}")
    public ResponseEntity<Trade> getTrade(@PathVariable String gameId, @PathVariable String tradeId) {
        Trade trade = tradeService.getTrade(gameId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trade, HttpStatus.OK);
    }

    @Operation (summary = "Accept a trade")
    @PutMapping("/{playerId}/accept-trade/{tradeId}")
    public ResponseEntity<Trade> acceptTrade(@PathVariable String gameId,@PathVariable String tradeId, @PathVariable String playerId) {
        Trade trade = tradeService.acceptTrade(gameId, playerId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation (summary = "Decline a trade")
    @DeleteMapping("/{playerId}/decline-trade/{tradeId}")   
    public ResponseEntity<Trade> declineTrade(@PathVariable String gameId,@PathVariable String tradeId, @PathVariable String playerId) {
        Trade trade = tradeService.declineTrade(gameId, playerId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }






    
}
