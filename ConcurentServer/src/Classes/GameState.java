package Classes;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import Enums.ConstructionTypeEdge;
import Enums.ConstructionTypeVertex;



public class GameState {

    private List<Player> players;
    private int currentPlayerIndex;
    private ReentrantReadWriteLock lock;
    private static final boolean isLockFair = true;
    private HashMap<String, Resources> playerResources = new HashMap<String, Resources>();



    
    public GameState(int numPlayers) {

       initPlayerResources();
        
        this.currentPlayerIndex = 0;
        this.lock = new ReentrantReadWriteLock(isLockFair);

    }

    private void initPlayerResources() {
   
        for (int i = 0; i < 4; i++) {
            Resources resources = new Resources();
            playerResources.put("Player" + i, resources);
        }
    }

    public List<Player> getPlayers() {
        lock.readLock().lock();
        try {
            return players;
        } finally {
            lock.readLock().unlock();
        }
    }


    public void rollDice(Player player) {
        lock.writeLock().lock();
        try {
            int roll = ThreadLocalRandom.current().nextInt(2, 13);
            System.out.println(player.name + " rolled a " + roll);
            // Implement resource distribution based on roll
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void placeConstruction(Player player, ConstructionTypeVertex build, ConstructionTypeEdge road, int location) {
        lock.writeLock().lock();
        try {
             
            // Implement construction placement logic
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void initiateTrade(Player player) {
        lock.writeLock().lock();
        try {
           
            // Implement trade logic
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void acceptTrade(Player player) {
        lock.writeLock().lock();
        try {
            // Implement trade acceptance logic
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void declineTrade(Player player) {
        lock.writeLock().lock();
        try {
            // Implement trade rejection logic
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Player getCurrentPlayer() {
        lock.readLock().lock();
        try {
            return players.get(currentPlayerIndex);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void nextTurn() {
        lock.writeLock().lock();
        try {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
