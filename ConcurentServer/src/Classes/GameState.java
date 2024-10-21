package Classes;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ThreadLocalRandom;


import Enums.ActionsType;
import Enums.ResourceType;


public class GameState {

    private int currentPlayerIndex;
    private ReentrantReadWriteLock lock;
    private static final boolean isLockFair = true;
    private HashMap<String, Resources> playerResources = new HashMap<String, Resources>();

    private HashMap<Integer, ArrayList<PlayerTiles>> diceRollResourceTypeMap = new HashMap<>() {{
        put(2, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.WOOD, ResourceType.WHEAT}),
                new PlayerTiles("2", new ResourceType[]{ResourceType.SHEEP, ResourceType.WOOD}),
                new PlayerTiles("4", new ResourceType[]{ResourceType.CLAY, ResourceType.WHEAT})
        )));
        put(3, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("2", new ResourceType[]{ResourceType.CLAY, ResourceType.WHEAT}),
                new PlayerTiles("3", new ResourceType[]{ResourceType.STONE}),
                new PlayerTiles("4", new ResourceType[]{ResourceType.SHEEP})
        )));
        put(4, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.WHEAT}),
                new PlayerTiles("2", new ResourceType[]{ResourceType.WOOD})
        )));
        put(5, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.SHEEP}),
                new PlayerTiles("3", new ResourceType[]{ResourceType.STONE})
        )));
        put(6, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.CLAY}),
                new PlayerTiles("3", new ResourceType[]{ResourceType.WHEAT})
        )));
        put(8, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("3", new ResourceType[]{ResourceType.SHEEP}),
                new PlayerTiles("2", new ResourceType[]{ResourceType.WHEAT}),
                new PlayerTiles("4", new ResourceType[]{ResourceType.STONE})
        )));
        put(9, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.CLAY, ResourceType.WHEAT}),
                new PlayerTiles("3", new ResourceType[]{ResourceType.WOOD})
        )));
        put(10, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.CLAY}),
                new PlayerTiles("3", new ResourceType[]{ResourceType.WOOD, ResourceType.SHEEP}),
                new PlayerTiles("4", new ResourceType[]{ResourceType.WOOD})
        )));
        put(11, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("1", new ResourceType[]{ResourceType.WHEAT, ResourceType.SHEEP}),
                new PlayerTiles("2", new ResourceType[]{ResourceType.WOOD})
        )));
        put(12, new ArrayList<PlayerTiles>(Arrays.asList(
                new PlayerTiles("4", new ResourceType[]{ResourceType.STONE}),
                new PlayerTiles("2", new ResourceType[]{ResourceType.SHEEP, ResourceType.WHEAT})
        )));
    }};

    public GameState(int numPlayers) {
        this.currentPlayerIndex = 0;
        this.lock = new ReentrantReadWriteLock(isLockFair);
        initPlayerResources();
    }

    private void initPlayerResources() {
        for (int i = 1; i <= 4; i++) {
            Resources resources = new Resources();
            playerResources.put("" + i, resources);
        }
    }

    private void distributeResources(int roll) {
        ArrayList<PlayerTiles> playerTiles = diceRollResourceTypeMap.get(roll);
        for (PlayerTiles playerTile : playerTiles) {
            Resources resources = playerResources.get(playerTile.getPlayerId());
            if (resources == null) {
                throw new IllegalArgumentException("Invalid player id");
            }
            for (ResourceType resource : playerTile.getResources()) {
                switch (resource) {
                    case WOOD:
                        resources.addResource(ResourceType.WOOD, 1);
                        break;
                    case STONE:
                        resources.addResource(ResourceType.STONE, 1);
                        break;
                    case CLAY:
                        resources.addResource(ResourceType.CLAY, 1);
                        break;
                    case SHEEP:
                        resources.addResource(ResourceType.SHEEP, 1);
                        break;
                    case WHEAT:
                        resources.addResource(ResourceType.WHEAT, 1);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid resource type");
                }
            }
        }
    }

    private void printResources() {
        for (Map.Entry<String, Resources> entry : playerResources.entrySet()) {
            System.out.println("\n\nPlayer " + entry.getKey());
            Resources resources = entry.getValue();
            for(Map.Entry<ResourceType, Integer> resource : resources.getResources().entrySet()) {
                System.out.println(resource.getKey() + ": " + resource.getValue());
            }
        }
    }
//
//    public List<Player> getPlayers() {
//        lock.readLock().lock();
//        try {
//            return players;
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//
    public void rollDice(Player player) {
        lock.writeLock().lock();
        try {
            int roll = ThreadLocalRandom.current().nextInt(2, 13);
            while(roll == 7) {
                roll = ThreadLocalRandom.current().nextInt(2, 13);
            }
            System.out.println(player.name + " rolled a " + roll + " at timestamp " + System.currentTimeMillis()/1000 + "s\n\n");
            distributeResources(roll);
            printResources();
        } finally {
            lock.writeLock().unlock();
        }
    }
//
//    public void placeConstruction(Player player, ConstructionTypeVertex build, ConstructionTypeEdge road, int location) {
//        lock.writeLock().lock();
//        try {
//
//            // Implement construction placement logic
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//


    public void initiateTrade(Player player) {
        lock.writeLock().lock();
        try {
//            System.out.println(player.name + " is initiating trade at timestamp " + System.currentTimeMillis()/1000 + "s\n\n");



        } finally {
            lock.writeLock().unlock();
        }
    }

//    public void acceptTrade(Player player) {
//        lock.writeLock().lock();
//        try {
//            // Implement trade acceptance logic
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//    public void declineTrade(Player player) {
//        lock.writeLock().lock();
//        try {
//            // Implement trade rejection logic
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public Player getCurrentPlayer() {
//        lock.readLock().lock();
//        try {
//            return players.get(currentPlayerIndex);
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public void nextTurn() {
//        lock.writeLock().lock();
//        try {
//            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }

    public void takeAction(Player currentPlayer) {
        for (ActionsType actionTaken : currentPlayer.actionsToTake) {
            System.out.println(currentPlayer.name + " is taking action: " + actionTaken);

            switch (actionTaken) {
                case ROLL_DICE:
                    this.rollDice(currentPlayer);
                    break;
//            case PLACE_SETTLEMENT:
//                gameState.placeConstruction(this, ConstructionTypeVertex.SETTLEMENT, null, 0);
//
//                break;
                case INITIATE_TRADE:
                    this.initiateTrade(currentPlayer);
                    break;
//            case PLACE_CITY:
//                gameState.placeConstruction(this, ConstructionTypeVertex.CITY, null, 0);
//                break;
//            case    PLACE_ROAD:
//                gameState.placeConstruction(this, null, ConstructionTypeEdge.ROAD, 0);
//                break;
//            case ACCEPT_TRADE:
//                gameState.acceptTrade(this);
//                break;
//            case DECLINE_TRADE:
//                gameState.declineTrade(this);
//                break;

                default:
                    System.out.println("Invalid action");
                    throw new IllegalArgumentException("Invalid action");
            }
        }
    }


}
