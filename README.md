# CatanCEBP
This is where our CEBP project will live
# Game API Specification

## Authentication Endpoints
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/profile
```

## Game Session Management
```
POST /api/games/create              # Create a new game session
<!-- GET  /api/games/list                # List available games -->
GET  /api/games/{gameId}            # Get game details
POST /api/games/{gameId}/join       # Join an existing game
POST /api/games/{gameId}/start      # Start the game (for host)
<!-- GET  /api/games/{gameId}/state      # Get current game state -->
```

## Resource Management
```
GET  /api/games/{gameId}/resources                    # Get player's current resources
POST /api/games/{gameId}/resources/gather/{type}      # Gather basic resources
GET  /api/games/{gameId}/resources/production-rates   # Get current resource production rates
```

## Building Management
```
GET  /api/games/{gameId}/buildings                    # List all player buildings
POST /api/games/{gameId}/buildings/construct          # Construct new building
POST /api/games/{gameId}/buildings/{buildingId}/upgrade    # Upgrade existing building
GET  /api/games/{gameId}/buildings/available          # Get available buildings to construct
GET  /api/games/{gameId}/buildings/{buildingId}/info  # Get specific building information
```

## Trading System
```
GET  /api/games/{gameId}/market                      # Get market status
<!-- GET  /api/games/{gameId}/market/prices               # Get current market prices -->
POST /api/games/{gameId}/market/create-offer         # Create trade offer
POST /api/games/{gameId}/market/accept-offer/{offerId}    # Accept existing offer
GET  /api/games/{gameId}/market/my-offers            # List player's active offers
DELETE /api/games/{gameId}/market/offers/{offerId}   # Cancel trade offer
```

## Player Interaction
```
GET  /api/games/{gameId}/players                     # List players in game
POST /api/games/{gameId}/players/trade              # Direct trade with player
<!-- POST /api/games/{gameId}/players/message            # Send message to player --> AMQP
```

## Game State and Victory
```
POST /api/games/{gameId}/verify-victory            # Verify victory conditions
GET  /api/games/{gameId}/leaderboard               # Get current standings
```

## Data Models

### Resource
```json
{
  "id": "string",
  "type": "wood|stone|food|metal|...",
  "amount": "number",
  "productionRate": "number",
  "storage": {
    "current": "number",
    "maximum": "number"
  }
}
```

### Building
```json
{
  "id": "string",
  "type": "string",
  "tier": "number",
  "level": "number",
  "position": {
    "x": "number",
    "y": "number"
  },
  "status": "constructing|active|upgrading",
  "productionData": {
    "resourceType": "string",
    "rate": "number"
  },
  "requirements": {
    "resources": [{
      "type": "string",
      "amount": "number"
    }],
    "buildings": [{
      "type": "string",
      "tier": "number"
    }]
  }
}
```

### Trade Offer
```json
{
  "id": "string",
  "seller": "string",
  "offering": [{
    "resourceType": "string",
    "amount": "number"
  }],
  "requesting": [{
    "resourceType": "string",
    "amount": "number"
  }],
  "status": "active|completed|cancelled",
  "created": "timestamp",
  "expires": "timestamp"
}
```

### Game State
```json
{
  "id": "string",
  "status": "waiting|active|completed",
  "players": [{
    "id": "string",
    "resources": "Resource[]",
    "buildings": "Building[]",
    "victoryProgress": "number"
  }],
  "market": {
    "offers": "TradeOffer[]",
    "prices": [{
      "resourceType": "string",
      "currentPrice": "number",
      "trend": "up|down|stable"
    }]
  },
  "created": "timestamp",
  "lastUpdated": "timestamp"
}
```
