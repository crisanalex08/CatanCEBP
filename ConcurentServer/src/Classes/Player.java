package Classes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Enums.ResourceType;

public class Player implements Runnable {
    private final int playerIndex;
    private final GameState state;
    private final Map<ResourceType, Integer> resources;
    private final List<Building> buildings;
    private final Random random;

    public Player(int playerIndex, GameState state) {
        this.playerIndex = playerIndex;
        this.state = state;
        this.resources = new ConcurrentHashMap<>();
        this.buildings = new ArrayList<>();
        this.random = new Random();

        // Starting resources
        for (ResourceType type : ResourceType.values()) {
            resources.put(type,1); // Starting amount for each player
        }
    }


    @Override
    public void run() {
        while (true) {
            synchronized(state) {
                // Check game end condition before processing turn
                if (state.isGameEnded()) {
                    break;
                }

                if (state.isPlayerTurn(playerIndex)) {
                    // Roll dice
                    int roll = random.nextInt(6) + 1;
                    System.out.println("Player " + playerIndex + " rolled " + roll + System.currentTimeMillis());

                    // Collect resources based on buildings
                    collectResources();


                    if (random.nextBoolean()) {
                        System.out.println("Player " + playerIndex + " initiated trade" + System.currentTimeMillis());
                        initiateRandomTrade();
                    }

                    // Try to build something
                    tryToBuild();

                    // End turn and check game end condition atomically
                    state.endTurn();
                    state.checkGameEndCondition();

                }


            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    private void collectResources() {
        for (Building building : buildings) {
            Map<ResourceType, Integer> production = building.getResourceProduction();
            System.out.println("Player " + playerIndex + " collecting resources" + System.currentTimeMillis());
            for (Map.Entry<ResourceType, Integer> entry : production.entrySet()) {
                addResource(entry.getKey(), entry.getValue());
                System.out.println("Player " + playerIndex + " collected " + entry.getValue() + " " + entry.getKey());
            }
        }
    }

    private void initiateRandomTrade() {

        Map<ResourceType, Integer> offering = new HashMap<>();
        Map<ResourceType, Integer> requesting = new HashMap<>();

        ResourceType offerType = ResourceType.values()[random.nextInt(ResourceType.values().length)];
        ResourceType requestType = ResourceType.values()[random.nextInt(ResourceType.values().length)];

        offering.put(offerType, 1);
        requesting.put(requestType, 1);

        Trade offer = new Trade(offering, requesting);
        state.initTradeOffer(playerIndex, offer);

    }

    private void tryToBuild() {
        Building building = state.getAvailableBuildings().get(random.nextInt(state.getAvailableBuildings().size()));
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

