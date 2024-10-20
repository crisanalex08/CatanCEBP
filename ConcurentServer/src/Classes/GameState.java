package Classes;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ThreadLocalRandom;

import Enums.ConstructionTypeEdge;
import Enums.ConstructionTypeVertex;


public class GameState {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private ReentrantReadWriteLock lock;
    private static final boolean isLockFair = true;

    public GameState(int numPlayers) {
        this.board = new Board();
        this.players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player("Player" + i, "Player " + i));
        }
        this.currentPlayerIndex = 0;
        this.lock = new ReentrantReadWriteLock(isLockFair);

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
            System.out.println(player.name + " placed a " + build + " at location " + location);
            // Implement construction placement logic
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void initiateTrade(Player player) {
        lock.writeLock().lock();
        try {
            System.out.println(player.name + " initiated a trade");
            // Implement trade logic
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
