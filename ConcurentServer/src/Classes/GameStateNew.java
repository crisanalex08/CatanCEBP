package Classes;

import Enums.ResourceType;
import Interfaces.Building;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameStateNew {
    private final int numPlayers;
    private ConcurrentHashMap<Integer, Resources> playersResources;
    private ConcurrentHashMap<Integer, List<Building>> playersBuildings;
    private ReentrantLock diceRollLock;

    private ReentrantReadWriteLock resourcesLock;
    private volatile boolean gameEnded;
    private volatile int winner = -1;
    private volatile int currentPlayer = 0;


    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public GameStateNew(int numPlayers) {
        this.numPlayers = numPlayers;
        this.playersResources = new ConcurrentHashMap<Integer, Resources>();
        this.playersBuildings = new ConcurrentHashMap<Integer, List<Building>>();
        this.diceRollLock = new ReentrantLock(true);
        
        this.gameEnded = false;
        this.resourcesLock = new ReentrantReadWriteLock();

        initPlayerResources();
        initPlayerBuildings();
    }
    private void initPlayerBuildings() {
    for (int i = 0; i < numPlayers; i++) {

        Building settlement = new Settlement();
        List<Building> buildings = new ArrayList<>(
                Collections.nCopies(2, settlement));

        playersBuildings.put(i,buildings);
    }
    }
    private void initPlayerResources() {
        for (int i = 0; i <numPlayers; i++) {
            Resources resources = new Resources();
            if(i == 0) {
                resources.addResource(ResourceType.WOOD, 2);
                resources.addResource(ResourceType.CLAY, 2);
                resources.addResource(ResourceType.SHEEP, 2);
                resources.addResource(ResourceType.WHEAT, 2);
            }
            else {
                resources.addResource(ResourceType.WOOD, 1);
                resources.addResource(ResourceType.CLAY, 1);
                resources.addResource(ResourceType.SHEEP, 1);
                resources.addResource(ResourceType.WHEAT, 1);
            }
            playersResources.put(i, resources);
        }
    }
    private int getRollDice() {
        return (int) (Math.random() * 11 + 2);
    
    }
   
    public void rollDice(Player player) {
        if(diceRollLock.tryLock()) 
        {
            try {
                setCurrentPlayer(currentPlayer);
                int roll = getRollDice();
                System.out.println( "!!Player " + player.getId() + " rolled a " + roll + " at " + System.currentTimeMillis());
                this.distributeResources(roll);            
            }catch (Exception e) {
              
            }
             finally {
                diceRollLock.unlock();
            }
        }
    }
    public void distributeResources(int roll){
        resourcesLock.writeLock().lock();
        try {
            System.out.println("Distributing resources for roll " + roll + " at " + System.currentTimeMillis());
        } finally {
            resourcesLock.writeLock().unlock();
        }
    }
    public boolean hasEnoughResources(Player player, Building building) {
        resourcesLock.readLock().lock();
        try {
            Resources playerResources = playersResources.get(player.getId());
            return building.getRequiredResources().entrySet().stream()
                    .allMatch(e -> playerResources.getQuantity(e.getKey()) >= e.getValue());
        } finally {
            resourcesLock.readLock().unlock();
        }
    }
    public void tryToBuild(Player player, Building building) throws InterruptedException {
        resourcesLock.readLock().lock();
        try {

            if(hasEnoughResources(player, building))
            {
                System.out.println("Player " + player.getId() + " has enough resources to build " + building.getName() + " at " + System.currentTimeMillis());
                building.getRequiredResources().forEach((resourceType, quantity) -> {
                    playersResources.get(player.getId()).removeResource(resourceType, quantity);
                });
                playersBuildings.get(player.getId()).add(building);
                System.out.println("Player " + player.getId() + " built " + building.getName() + " at " + System.currentTimeMillis());
                
                checkGameEndCondition(currentPlayer);
                
              


            } else {
                System.out.println("Player " + player.getId() + " does not have enough resources to build " + building.getName() + " at " + System.currentTimeMillis());
            }
        } 
        finally
        {
            resourcesLock.readLock().unlock();
        }
    }
    public boolean isGameEnded() {
        System.out.println("Game ended: " + gameEnded);
        return gameEnded;
    }
    public void checkGameEndCondition(int playerId) throws InterruptedException {
        System.out.println("Check " + playersBuildings.get(playerId).size());
        if(playersBuildings.get(playerId).size() >= 3) {
            
            gameEnded = true;
            winner = playerId;

            throw new InterruptedException("Player " + playerId + " has won the game!");

        
        }
     

       
    }

    public int getWinner() {
        return winner;
    }
}
