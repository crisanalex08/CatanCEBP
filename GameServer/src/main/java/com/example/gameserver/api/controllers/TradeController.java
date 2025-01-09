package com.example.gameserver.api.controllers;

import java.time.LocalDateTime;
import java.util.List;

import com.example.gameserver.websocket.GamesWebSocketHandler;
import com.example.gameserver.api.dto.GameMessage;
import com.example.gameserver.enums.ResourceType;
import com.example.gameserver.enums.TradeStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gameserver.api.dto.TradeRequest;
import com.example.gameserver.entity.Trade;
import com.example.gameserver.entity.User;
import com.example.gameserver.services.TradeService;
import com.example.gameserver.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.socket.TextMessage;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/games/{gameId}/trades/")
@Tag(name = "Trade Controller", description = "Operations to manage trades")
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;
    private final GamesWebSocketHandler gamesWebSocketHandler;

    private final Map<String, ResourceType> ResourceMap = new HashMap<>() {{
        put("WOOD", ResourceType.WOOD);
        put("CLAY", ResourceType.CLAY);
        put("STONE", ResourceType.STONE);
        put("SHEEP", ResourceType.SHEEP);
        put("WHEAT", ResourceType.WHEAT);
        put("GOLD", ResourceType.GOLD);
    }};

    @Autowired
    public TradeController(TradeService tradeService, UserService userService,GamesWebSocketHandler gamesWebSocketHandler) {
        this.gamesWebSocketHandler = gamesWebSocketHandler;
        this.userService = userService;
        this.tradeService = tradeService;
    }

    @Operation (summary = "Make a merchant trade")
    @PostMapping("/merchant-trade")
    public ResponseEntity<?> createMerchantTrade(@RequestBody TradeRequest request) {
        try{
        TradeStatus tradeStatus = tradeService.merchantTrade(request);
        if (tradeStatus == TradeStatus.CANCELLED) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String playerName = getUserName(request.getPlayerId());
        sendSystemMessage(request.getGameId(), "Player: " + playerName + " has done a merchant trade.");
        return new ResponseEntity<>(tradeStatus, HttpStatus.CREATED);
        }
        catch (Exception e) {
            log.error("TradeController: Error creating merchant trade! ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation (summary = "List a player trade")
    @PostMapping("/player-trade")
    public ResponseEntity<?> createPlayerTrade(@RequestBody TradeRequest request) {
        try{
        Trade trade = tradeService.playerTrade(request);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String playerName = getUserName(request.getPlayerId());
        sendSystemMessage(request.getGameId(), "Player: " + playerName  +" has created a trade.");
        return new ResponseEntity<>(trade, HttpStatus.CREATED);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("TradeController: Error creating player trade! ", e);
            return ResponseEntity.internalServerError().body("Failed to create trade because: " + e.getMessage());
         
        }
    }

    @Operation (summary = "Get all active trades that a player can accept")
    @GetMapping("/{playerId}/trades")
    public ResponseEntity<?> getMyActiveTrades(@PathVariable Long gameId, @PathVariable Long playerId) {
        try{
        List<Trade> trades = tradeService.getMyActiveTrades(gameId, playerId);
        if (trades == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(trades, HttpStatus.OK);
        }catch(Exception e) {
            log.error("TradeController: Error getting active trades! ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation (summary = "Accept a trade")
    @PostMapping("/accept-trade/{tradeId}")
    public ResponseEntity<Trade> acceptTrade(@PathVariable Long gameId,@PathVariable Long tradeId) {
        TradeStatus tradeStatus = tradeService.acceptTrade(gameId, tradeId);
        if (tradeStatus == TradeStatus.CANCELLED) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sendSystemMessage(gameId, "Trade: " + tradeId + " has been accepted.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation (summary = "Decline a trade")
    @DeleteMapping("/decline-trade/{tradeId}")
    public ResponseEntity<Trade> declineTrade(@PathVariable Long gameId,@PathVariable Long tradeId) {
        Trade trade = tradeService.declineTrade(gameId, tradeId);
        if (trade == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sendSystemMessage(gameId, "Trade: " + tradeId + " has been declined.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void sendSystemMessage(Long gameId, String content){
        GameMessage message = new GameMessage();
        message.setGameId(gameId);
        message.setSender("System");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            String jsonMessage = mapper.writeValueAsString(message);
            gamesWebSocketHandler.broadcastToLobby(gameId.toString(), new TextMessage(jsonMessage));
        }
        catch (Exception e) {
            log.error("GamePlayController: Error sending system message, could not map the message to string! ", e);
        }
    }
    private String getUserName(Long playerId) {
        User user = userService.getUserById(playerId);
        return user.getName();
    }
}
