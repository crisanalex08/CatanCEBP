package com.example.gameserver.api.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.gameserver.entity.Resources;
import com.example.gameserver.services.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.concurrent.Future;


@CrossOrigin
@RestController
@RequestMapping("/api/games/{gameId}/resources/")

@Tag(name = "Resource Controller", description = "Operations to manage resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
    @Operation(summary = "Initialize player resources")
    @PostMapping("/{playerId}/initialize")
    public ResponseEntity<Resources> initializePlayerResources(@PathVariable Long gameId,@PathVariable Long playerId) {
        String result = resourceService.initializePlayerResources(gameId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get player resources")
    @GetMapping("/{playerId}")
    public ResponseEntity<Resources> getPlayerResources(@PathVariable Long gameId,@PathVariable Long playerId) {
        Future<Resources> futureResources = resourceService.getPlayerResources(gameId, playerId);
        try{
            Resources resources = futureResources.get();
            if (resources == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(resources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
    }

   
}
