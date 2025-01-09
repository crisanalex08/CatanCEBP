# CatanCEBP

CatanCEBP is a multiplayer online game inspired by the classic board game "Catan". This project includes a game server, a concurrent server for simulations, and a web-based user interface for players to interact with the game.
## Project Structure
### Directories

- **ConcurentServer**: Contains the concurrent server for game simulations.(First Milestone)
- **GameServer**: Contains the main game server code.
- **GameUI**: Contains the Angular-based user interface for the game.

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js and npm
- Angular CLI

### Setting Up the Game Server
1. Navigate to the [GameServer](http://_vscodecontentref_/#%7B%22uri%22%3A%7B%22%24mid%22%3A1%2C%22fsPath%22%3A%22c%3A%5C%5CUsers%5C%5Csraul%5C%5CIdeaProjects%5C%5CCatanCEBP%5C%5CGameServer%22%2C%22_sep%22%3A1%2C%22path%22%3A%22%2Fc%3A%2FUsers%2Fsraul%2FIdeaProjects%2FCatanCEBP%2FGameServer%22%2C%22scheme%22%3A%22file%22%7D%7D) directory:
    ```sh
    cd GameServer
    ```

2. Build the project using Maven:
    ```sh
    ./mvnw clean install
    ```

3. Run the game server:
    ```sh
    ./mvnw spring-boot:run
    ```

### Setting Up the Game UI

1. Navigate to the [UI](http://_vscodecontentref_/#%7B%22uri%22%3A%7B%22%24mid%22%3A1%2C%22fsPath%22%3A%22c%3A%5C%5CUsers%5C%5Csraul%5C%5CIdeaProjects%5C%5CCatanCEBP%5C%5CGameUI%5C%5CUI%22%2C%22_sep%22%3A1%2C%22path%22%3A%22%2Fc%3A%2FUsers%2Fsraul%2FIdeaProjects%2FCatanCEBP%2FGameUI%2FUI%22%2C%22scheme%22%3A%22file%22%7D%7D) directory:
    ```sh
    cd GameUI/UI
    ```

2. Install the dependencies:
    ```sh
    npm install
    ```

3. Run the development server:
    ```sh
    ng serve
    ```

4. Open your browser and navigate to [`http://localhost:4200/`]

## **Players**
- **Min:** 2 players.
- **Max:** 4 players.
---

## **Initial Setup**
### **Settlement**
- Each player starts with a **Settlement**.
- Each Settlement produces 2 different types of resources (random) from: **Wood**, **Clay**, **Wheat**, each in quantity of **1**.
- Each resource is associated with a number from **1 to 6** (corresponding to dice rolls)..  
  - **Exemple:**  
    - **Settlement Player 1**: Wood - Nr. 3, Clay - Nr. 1.  
    - **Settlement Player 2**: Clay - Nr. 4, Wheat - Nr. 3.

---

## **Buildings**

### **1. Town**
- Can be built by upgrading a Settlement.
- **Cost:**  
  - **2 Wood, 2 Clay, 2 Wheat.**
- Produces **2 new types of resources** (Random) from:  
  - **Stone**, **Sheep**, **Gold**, each in quantity of **1**.
- Each resource is associated with a number from **1 la 6**.  
  - **Exemple:**  
    - **Town Player 1**: Metal - Nr. 3, Stone - Nr. 1.  
    - **Town Player 2**: Stone - Nr. 4, Sheep - Nr. 3.

### **2. Castle**
- Can be built by upgrading a Town.
- **Cost:**  
  - **3 Gold, 3 Stone, 3 Wood, 3 Sheep.**
- Building a Castle = **Victory in the game**.

---

## **General Rules**
1. **Maximum 3 Constructions per Player**
   - Each player can have at most **3 constructions** on the map.
   - Construction order: **Settlement → Town → Castle**.

2. **Trade**
   - Free negotiations between players.
   - Trades with merchant: **3 resources of the same type for 1 resource of choice.**

---

## **Objective**
Build a **Castle** to win the game. Plan resources carefully, trade wisely, and optimize dice rolls to achieve victory!


# Game API Specification


## Game Session Management
```
POST /api/games/create              # Create a new game session
GET  /api/games/list                # List available games
GET  /api/games/{gameId}            # Get game details
POST /api/games/{gameId}/join       # Join an existing game
POST /api/games/{gameId}/start      # Start the game (for host)
PUT /api/games/{gameId}/leave       # Leave an existing game
```

## Resource Management
```
GET  /api/games/{gameId}/resources/{playerId}                 # Get player's current resources
POST /api/games/{gameId}/resources/{playerId}/initialize      # Initialize resources for players in a game.
GET  /api/games/{gameId}/resources/production-rates           # Get current resource production rates
```

## Building Management
```
GET  /api/games/{gameId}/buildings                         # List all player buildings
POST /api/games/{gameId}/buildings/construct               # Construct new building
POST /api/games/{gameId}/buildings/{playerId}/{buildingId}/upgrade    # Upgrade existing building
GET  /api/games/{gameId}/buildings/available               # Get available buildings to construct
GET  /api/games/{gameId}/buildings/{buildingId}/info       # Get specific building information
```

## Trading System
```
GET  /api/games/{gameId}/trades/merchant-trade            # Create a trade with merchant (3 to 1 )
POST /api/games/{gameId}/trades/player-trade              # Create trade offer
POST /api/games/{gameId}/trades/accept-trade/{tradeId}    # Accept existing trade
DELETE /api/games/{gameId}/trades/decline-trade/{tradeId} # Decline existing trade
GET  /api/games/{gameId}/trades/{playerId}/trades         # List player's active offers

```

## Gameplay Mechanism
```
POST /api/gameplay/{gameId}/start                           # Start an existing game
POST /api/gameplay/{gameId}/roll/{playerId}                 # Roll Dice and Distribute Resources
POST /api/gameplay/{gameId}/construct/{playerId}            # Build Settlement
POST /api/gameplay/{gameId}/upgrade/{playerId}/{buildingId} # Upgrade Building
GET  /api/gameplay/{gameId}/buildings                       # List All players Building 
```
