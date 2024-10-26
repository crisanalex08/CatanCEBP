package Classes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import Enums.ResourceType;


public class GameState {

    private final Map<ResourceType,Integer> resources;
    private final List<Building> availableBuildings;
    private final ReentrantLock buildingLock;
    private final ReentrantLock resourceLock;
    private final AtomicInteger currentPlayer;
    private final Player[] players;
    private volatile boolean gameEnded;

    public GameState() {
        this.resources = new ConcurrentHashMap<>();
        this.availableBuildings = new ArrayList<>();
        this.buildingLock = new ReentrantLock();
        this.resourceLock = new ReentrantLock();
        this.currentPlayer = new AtomicInteger(0);
        this.players = new Player[2];
        this.gameEnded = false;

        for (ResourceType resourceType : ResourceType.values()) {
            resources.put(resourceType,50);
        }
        initBuildings();
    }
    private  void initBuildings()
    {
        Map<ResourceType,Integer>  settlementCost = new HashMap<>();
        settlementCost.put(ResourceType.WOOD,1);
        settlementCost.put(ResourceType.CLAY,1);
        settlementCost.put(ResourceType.WHEAT,1);
        settlementCost.put(ResourceType.SHEEP,1);

        Map<ResourceType,Integer>  cityCost = new HashMap<>();
        cityCost.put(ResourceType.STONE,3);
        cityCost.put(ResourceType.WHEAT,2);

        Map<ResourceType,Integer> settlementProduction = new HashMap<>();
        settlementProduction.put(ResourceType.WOOD,1);
        settlementProduction.put(ResourceType.CLAY,1);
        settlementProduction.put(ResourceType.WHEAT,1);
        settlementProduction.put(ResourceType.SHEEP,1);
        settlementProduction.put(ResourceType.STONE,1);

        Map<ResourceType,Integer> cityProduction = new HashMap<>();
        cityProduction.put(ResourceType.WOOD,2);
        cityProduction.put(ResourceType.CLAY,2);
        cityProduction.put(ResourceType.WHEAT,2);
        cityProduction.put(ResourceType.SHEEP,2);
        cityProduction.put(ResourceType.STONE,2);

        availableBuildings.add(new Building("Settlement",settlementCost,settlementProduction));
        availableBuildings.add(new Building("City",cityCost,cityProduction));

    }
    public void addPlayer(int playerIndex, Player player) {
        players[playerIndex] = player;
    }

    public boolean isPlayerTurn(int playerIndex) {
        return currentPlayer.get() == playerIndex;
    }

    public synchronized void endTurn() {
        currentPlayer.updateAndGet(current -> (current + 1) % 2);
    }

    public boolean build(int playerIndex, Building building) {
        buildingLock.lock();
        try {
            if(!isPlayerTurn(playerIndex)) {
                return false;
            }
            Player player = players[playerIndex];
            Map<ResourceType, Integer> cost = building.getRequiredResources();
            for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
                if(player.getResource(entry.getKey()) < entry.getValue()) {
                    return false;
                }

            }
            for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
                System.out.println("Player " + playerIndex +  " " + player.getResource(entry.getKey()) + " " + entry.getKey());
                player.removeResource(entry.getKey(), entry.getValue());
                System.out.println("Player " + playerIndex + " deducted " + entry.getValue() + " " + entry.getKey());
                System.out.println("Player " + playerIndex +  " " + player.getResource(entry.getKey()) + " " + entry.getKey());

            }
            player.addBuilding(building);
            return true;
        } finally {
            buildingLock.unlock();
        }
    }
    public void initTradeOffer(int playerIndex, Trade trade)
    {
        if(!isPlayerTurn(playerIndex))
        {
            return;
        }
        Player initiator = players[playerIndex];
        Player receiver = players[(playerIndex + 1) % 2];


        for (Map.Entry<ResourceType, Integer> entry : trade.getOffering().entrySet()) {
            if(initiator.getResource(entry.getKey()) < entry.getValue()) {
                return;
            }
        }
        receiver.handleTradeOffer(trade);
        try{
            if(!trade.waitForResponse(5000))
            {
                trade.setRejected();
                return;
            }
        }catch (InterruptedException e)
        {
            return;
        }
        if(trade.isAccepted())
        {

            resourceLock.lock();
            try {
                for (Map.Entry<ResourceType, Integer> entry : trade.getOffering().entrySet()) {
                    initiator.removeResource(entry.getKey(), entry.getValue());
                    receiver.addResource(entry.getKey(), entry.getValue());
                    System.out.println("Player " + playerIndex + " transferred " + entry.getValue() + " " + entry.getKey());
                    System.out.println("Player " + playerIndex +  " " + initiator.getResource(entry.getKey()) + " " + entry.getKey());
                }
                for (Map.Entry<ResourceType, Integer> entry : trade.getRequesting().entrySet()) {
                    receiver.removeResource(entry.getKey(), entry.getValue());
                    initiator.addResource(entry.getKey(), entry.getValue());
                    System.out.println("Player " + playerIndex + " received " + entry.getValue() + " " + entry.getKey());
                    System.out.println("Player " + playerIndex +  " " + initiator.getResource(entry.getKey()) + " " + entry.getKey());
                }
            } finally {
                resourceLock.unlock();
            }
        }

    }
    public boolean isGameEnded() {
        return gameEnded;
    }

    public synchronized void checkGameEndCondition() {
        if(!gameEnded) {
            for (Player player : players) {
                if(player.getBuildings().size() >=3 ){
                    gameEnded = true;
                    System.out.println("Player " + player.getPlayerIndex() + " with " +
                            player.getBuildings() + " buildings won the game at timestamp " +
                            System.currentTimeMillis());
                    break;
                }
            }
        }
    }

    public List<Building> getAvailableBuildings() {
        return new ArrayList<>(availableBuildings);
    }


}
