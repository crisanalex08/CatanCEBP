//package Classes;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.Condition;
//import Enums.ResourceType;
//import Interfaces.Building;
//
//
//public class GameState {
//
//
//    private final ReentrantLock buildingLock;
//    private final ReentrantLock resourceLock;
//    private final ReentrantLock turnLock;
//    private final Condition turnCondition;
//    private final AtomicInteger currentPlayer;
//    private final PlayerOld[] playerOlds;
//    private volatile boolean gameEnded;
//
//    public GameState() {
//        this.buildingLock = new ReentrantLock();
//        this.resourceLock = new ReentrantLock();
//        this.turnLock = new ReentrantLock(true);
//        this.turnCondition = turnLock.newCondition();
//        this.currentPlayer = new AtomicInteger(0);
//        this.playerOlds = new PlayerOld[3];
//        this.gameEnded = false;
//    }
//
//
//
//
//    public void addPlayer(int playerIndex, PlayerOld playerOld) {
//        playerOlds[playerIndex] = playerOld;
//    }
//
//
//
//
//
//    public void endTurn() {
//        turnLock.lock();
//        try {
//            currentPlayer.updateAndGet(current -> (current + 1) % 3);
//            turnCondition.signalAll();
//        } finally {
//            turnLock.unlock();
//        }
//    }
//
//    public boolean build(int playerIndex, Building building) {
//        buildingLock.lock();
//        try {
//            if(!isPlayerTurn(playerIndex)) {
//                return false;
//            }
//            PlayerOld playerOld = playerOlds[playerIndex];
//            Map<ResourceType, Integer> cost = building.getRequiredResources();
//            for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
//                if(playerOld.getResource(entry.getKey()) < entry.getValue()) {
//                    System.out.println("Player " + playerIndex + " does not have enough resources to build " + building.getName());
//                    return false;
//                }
//
//            }
//            for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
//                System.out.println("Player " + playerIndex +  " " + playerOld.getResource(entry.getKey()) + " " + entry.getKey());
//                playerOld.removeResource(entry.getKey(), entry.getValue());
//                System.out.println("Player " + playerIndex + " deducted " + entry.getValue() + " " + entry.getKey());
//                System.out.println("Player " + playerIndex +  " " + playerOld.getResource(entry.getKey()) + " " + entry.getKey());
//
//            }
//            playerOld.addBuilding(building);
//            return true;
//        } finally {
//            buildingLock.unlock();
//        }
//    }
//    public void initTradeOffer(int playerIndex, Trade trade)
//    {
//        if(!isPlayerTurn(playerIndex))
//        {
//            return;
//        }
//        PlayerOld initiator = playerOlds[playerIndex];
//        PlayerOld receiver = playerOlds[(playerIndex + 1) % 3];
//
//
//        for (Map.Entry<ResourceType, Integer> entry : trade.getOffering().entrySet()) {
//            if(initiator.getResource(entry.getKey()) < entry.getValue()) {
//                return;
//            }
//        }
//        receiver.handleTradeOffer(trade);
//        try{
//            if(!trade.waitForResponse(5000))
//            {
//                trade.setRejected();
//                return;
//            }
//        }catch (InterruptedException e)
//        {
//            return;
//        }
//        if(trade.isAccepted())
//        {
//
//            resourceLock.lock();
//            try {
//                for (Map.Entry<ResourceType, Integer> entry : trade.getOffering().entrySet()) {
//                    initiator.removeResource(entry.getKey(), entry.getValue());
//                    receiver.addResource(entry.getKey(), entry.getValue());
//                    System.out.println("Player " + playerIndex + " transferred " + entry.getValue() + " " + entry.getKey());
//                    System.out.println("Player " + playerIndex +  " " + initiator.getResource(entry.getKey()) + " " + entry.getKey());
//                }
//                for (Map.Entry<ResourceType, Integer> entry : trade.getRequesting().entrySet()) {
//                    receiver.removeResource(entry.getKey(), entry.getValue());
//                    initiator.addResource(entry.getKey(), entry.getValue());
//                    System.out.println("Player " + playerIndex + " received " + entry.getValue() + " " + entry.getKey());
//                    System.out.println("Player " + playerIndex +  " " + initiator.getResource(entry.getKey()) + " " + entry.getKey());
//                }
//            } finally {
//                resourceLock.unlock();
//            }
//        }
//
//    }
//    public boolean isGameEnded() {
//        return gameEnded;
//    }
//
//    public void distributeResources(int diceRoll, int playerIndex) {
//        resourceLock.lock();
//        try {
//            for (PlayerOld playerOld : playerOlds) {
//                for (Building building : playerOld.getBuildings()) {
//                    if (building instanceof Settlement) {
//                        Settlement settlement = (Settlement) building;
//                        Map<Integer, ResourceType> diceRollResources = settlement.getDiceRollResources();
//                        for (Map.Entry<Integer, ResourceType> entry : diceRollResources.entrySet()) {
//                            if (entry.getKey() == diceRoll) {
//                                playerOld.addResource(entry.getValue(), 1);
//                                System.out.println("Player " + playerOld.getPlayerIndex() +
//                                    " received " + 1 + " " + entry.getValue());
//                            }
//                        }
//                    }
//                }
//            }
//        } finally {
//            resourceLock.unlock();
//        }
//    }
//
//    public void checkGameEndCondition() {
//        turnLock.lock();
//        try {
//            if (!gameEnded) {
//                for (PlayerOld playerOld : playerOlds) {
//                    if (playerOld.getBuildings().size() >= 4) {
//                        gameEnded = true;
//                        System.out.println("Player " + playerOld.getPlayerIndex() +
//                            " with " + playerOld.getBuildings() +
//                            " buildings won the game at timestamp " +
//                            System.currentTimeMillis());
//                        turnCondition.signalAll();
//                        break;
//                    }
//                }
//            }
//        } finally {
//            turnLock.unlock();
//        }
//    }
//
//
//
//
//}
