package Classes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Enums.ResourceType;
import Interfaces.Building;

public class Player implements Runnable {
    private final int playerIndex;
    private final GameState state;
    private final ConcurrentHashMap<ResourceType, Integer> resources;
    private final List<Building> buildings;
    private final Random random;

    public Player(int playerIndex, GameState state) {
        this.playerIndex = playerIndex;
        this.state = state;
        this.resources = new ConcurrentHashMap<>();
        this.buildings = new ArrayList<>();
        this.random = new Random();

        for (ResourceType resourceType : ResourceType.values()) {
            resources.put(resourceType, 1);
        }
     
       buildings.add(new Settlement());
       buildings.add(new Settlement());
       System.out.println("Player " + playerIndex + " created with buildings: " + buildings);

    }


    @Override
    public void run() {
        while (!state.isGameEnded()) {
            try {
     
                state.waitForTurn(playerIndex);

                if (state.isGameEnded()) {
                    break;
                }
                
                
                System.out.println("Player " + playerIndex + " starting turn at " + System.currentTimeMillis());
                
              
                int roll = random.nextInt(11) + 2;
                System.out.println("Player " + playerIndex + " rolled " + roll + " at " + System.currentTimeMillis());
                state.distributeResources(roll, playerIndex);

                
                if (random.nextBoolean()) {
                    System.out.println("Player " + playerIndex + " initiated trade at " + System.currentTimeMillis());
                    initiateRandomTrade();
                }

               
                tryToBuild();

              
                state.checkGameEndCondition();
                state.endTurn();

              
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    

    private void initiateRandomTrade() {

        Map<ResourceType, Integer> offering = new HashMap<>();
        Map<ResourceType, Integer> requesting = new HashMap<>();

        ResourceType offerType = Collections.max(resources.entrySet(), Map.Entry.comparingByValue()).getKey();
      
        ResourceType requestType = Collections.min(resources.entrySet(), Map.Entry.comparingByValue()).getKey();
        offering.put(offerType, 1);
        requesting.put(requestType, 1);

        Trade offer = new Trade(offering, requesting);
        state.initTradeOffer(playerIndex, offer);

    }

    private void tryToBuild() {
        Building building = new Settlement();
        if (state.build(playerIndex, building)) {
            System.out.println("Player " + playerIndex + " built " + building.getName() + System.currentTimeMillis());
        }
        else {
            System.out.println("Player " + playerIndex + " failed to build " + building.getName() + System.currentTimeMillis());
        }
    }

    public void handleTradeOffer(Trade offer) {

        boolean canTrade = true;
        for (Map.Entry<ResourceType, Integer> entry : offer.getRequesting().entrySet()) {
            if (getResource(entry.getKey()) < entry.getValue()) {
                canTrade = false;
                break;
            }
        }

        if (canTrade) {
            offer.setAccepted();
            System.out.println("Player " + playerIndex + " accepted trade offer" + System.currentTimeMillis());


        } else {
            offer.setRejected();
            System.out.println("Player " + playerIndex + " declined trade offer" + System.currentTimeMillis());
        }

    }

    public synchronized int getResource(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    public synchronized void addResource(ResourceType type, int amount) {
        resources.put(type, resources.getOrDefault(type, 0) + amount);
    }

    public synchronized void removeResource(ResourceType type, int amount) {
        int currentAmount = resources.getOrDefault(type, 0);
        if (currentAmount < amount) {
            throw new IllegalStateException("Insufficient resources");
        }
        resources.put(type, currentAmount - amount);
    }

    public synchronized void addBuilding(Building building) {
        buildings.add(building);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }



}
