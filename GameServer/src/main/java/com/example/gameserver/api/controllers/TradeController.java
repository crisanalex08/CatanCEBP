package com.example.gameserver.api.controllers;

import java.util.List;

import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.enums.TradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gameserver.api.dto.TradeRequest;
import com.example.gameserver.entity.Trade;
import com.example.gameserver.services.TradeService;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/games/{gameId}/trades/")
@Tag(name = "Trade Controller", description = "Operations to manage trades")
public class TradeController {

    private final TradeService tradeService;

    private final Map<String, ResourceType> ResourceMap = new HashMap<>() {{
        put("WOOD", ResourceType.WOOD);
        put("CLAY", ResourceType.CLAY);
        put("STONE", ResourceType.STONE);
        put("SHEEP", ResourceType.SHEEP);
        put("WHEAT", ResourceType.WHEAT);
        put("GOLD", ResourceType.GOLD);
    }};

    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Operation (summary = "Make a merchant trade")
    @PostMapping("/merchant-trade")
    public ResponseEntity<TradeStatus> createMerchantTrade(@RequestBody TradeRequest request) {
        TradeStatus tradeStatus = tradeService.merchantTrade(request);
        if (tradeStatus == TradeStatus.CANCELLED) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tradeStatus, HttpStatus.CREATED);
    }

    @Operation (summary = "List a player trade")
    @PostMapping("/player-trade")
    public ResponseEntity<Trade> createPlayerTrade(@RequestBody TradeRequest request) {
        Trade trade = tradeService.playerTrade(request);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trade, HttpStatus.CREATED);

    }

    @Operation (summary = "Get all active trades that a player can accept")
    @GetMapping("/{playerId}/trades")
    public ResponseEntity<List<Trade>> getMyActiveTrades(@PathVariable Long gameId, @PathVariable Long playerId) {
        List<Trade> trades = tradeService.getMyActiveTrades(gameId, playerId);
        if (trades == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trades, HttpStatus.OK);
    }

    @Operation (summary = "Accept a trade")
    @PutMapping("/{playerId}/accept-trade/{tradeId}")
    public ResponseEntity<Trade> acceptTrade(@PathVariable Long gameId,@PathVariable Long tradeId, @PathVariable Long playerId) {
        TradeStatus tradeStatus = tradeService.acceptTrade(gameId, playerId, tradeId);
        if (tradeStatus == TradeStatus.CANCELLED) {
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
