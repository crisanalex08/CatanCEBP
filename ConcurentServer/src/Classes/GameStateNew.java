package Classes;

import Enums.ResourceType;
import Interfaces.IBuilding;

import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.Map;

public class GameStateNew {
    private final int numPlayers;
    private ConcurrentHashMap<Integer, Resources> playersResources;
    private ConcurrentHashMap<Integer, List<IBuilding>> playersBuildings;
    private ReentrantLock diceRollLock;

    private ReentrantReadWriteLock resourcesLock;
    private volatile AtomicBoolean gameEnded;
    private volatile int currentPlayer = 0;
    private final ReentrantReadWriteLock distributionLock = new ReentrantReadWriteLock();


    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public GameStateNew(int numPlayers) {
        this.numPlayers = numPlayers;
        this.playersResources = new ConcurrentHashMap<Integer, Resources>();
        this.playersBuildings = new ConcurrentHashMap<Integer, List<IBuilding>>();
        this.diceRollLock = new ReentrantLock(true);
        
        this.gameEnded = new AtomicBoolean(false);
        this.resourcesLock = new ReentrantReadWriteLock();

        initPlayerResources();
        initPlayerBuildings();
    }
    private void initPlayerBuildings() {
    for (int i = 0; i < numPlayers; i++) {

        IBuilding settlement = new Settlement();
        List<IBuilding> buildings = new ArrayList<>(
                Collections.nCopies(1, settlement));

        playersBuildings.put(i,buildings);
    }
    }
    private void initPlayerResources() {
        for (int i = 0; i <numPlayers; i++) {
            Resources resources = new Resources();
          
                resources.addResource(ResourceType.WOOD, 1);
                resources.addResource(ResourceType.CLAY, 1);
                resources.addResource(ResourceType.SHEEP, 1);
                resources.addResource(ResourceType.WHEAT, 1);
            
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
       
        distributionLock.writeLock().lock();
        try {
            for (int i = 0; i < numPlayers; i++) {
                final int playerId = i;
                
                Map<ResourceType, Integer> resourcesToAdd = new HashMap<>();
                
                List<IBuilding> buildings = playersBuildings.get(playerId);
                for (IBuilding building : buildings) {
                    if (building instanceof Settlement) {
                        Settlement settlement = (Settlement) building;
                        ResourceType resource = settlement.getDiceRollResources().get(roll);
                        if (resource != null) {
                            resourcesToAdd.merge(resource, 1, Integer::sum);
                        }
                    }
                }
                
                
                if (!resourcesToAdd.isEmpty()) {
                    resourcesLock.writeLock().lock();
                    try {
                        Resources playerResources = playersResources.get(playerId);
                        resourcesToAdd.forEach((resource, amount) -> {
                            playerResources.addResource(resource, amount);
                            System.out.println("Player " + playerId + " received " + resource + " at " + System.currentTimeMillis());
                        });
                        System.out.println("Player " + playerId + " has resources: " + playerResources.getResources() + " at " + System.currentTimeMillis());
                    } finally {
                        resourcesLock.writeLock().unlock();
                    }
                }
            }
        } finally {
            distributionLock.writeLock().unlock();
        }
    }
    public boolean hasEnoughResources(Player player, IBuilding building) {
        resourcesLock.readLock().lock();
        try {
            Resources playerResources = playersResources.get(player.getId());
            return building.getRequiredResources().entrySet().stream()
                    .allMatch(e -> playerResources.getQuantity(e.getKey()) >= e.getValue());
        } finally {
            resourcesLock.readLock().unlock();
        }
    }
    public void tryToBuild(Player player, IBuilding building)   {
        

        resourcesLock.readLock().lock();
        try {
           
            if(gameEnded.get()) {
                return;
            }
            
            if(hasEnoughResources(player, building))
            {
                System.out.println("Player " + player.getId() + " has enough resources to build " + building.getName() + " at " + System.currentTimeMillis());
                building.getRequiredResources().forEach((resourceType, quantity) -> {
                    playersResources.get(player.getId()).removeResource(resourceType, quantity);
                });

                System.out.println("Player " + player.getId() + " has " + "resources: " + playersResources.get(player.getId()).getResources() + " at " + System.currentTimeMillis());
                playersBuildings.get(player.getId()).add(building);

                System.out.println("Player " + player.getId() + " built " + building.getName() + " at " + System.currentTimeMillis());

                checkGameEndCondition();
            } else {
                initTrade(player);
                System.out.println("Player " + player.getId() + " does not have enough resources to build " + building.getName() + " at " + System.currentTimeMillis());
                return;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            resourcesLock.readLock().unlock();
        }
    }
    public void initTrade(Player player) {
        System.out.println("Player " + player.getId() + " is trading at " + System.currentTimeMillis());
    }
    public boolean isGameEnded() {
        return gameEnded.get();
    }
    public void checkGameEndCondition()  {

        for (int i = 0; i < numPlayers; i++) {
            if (playersBuildings.get(i).size() >= 3) {
                gameEnded.set(true);
                System.out.println("Player " + i + " has won the game at " + System.currentTimeMillis());
                break;
            }
        }
    }
      
       

  
}
