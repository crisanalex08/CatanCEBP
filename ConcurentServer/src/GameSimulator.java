import Classes.GameState;
import Classes.Player;
import Enums.ActionsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSimulator {
    private GameState gameState;
    private List<Player> players;
    private int numOfRounds;
    private ArrayList<Thread> threads = new ArrayList<Thread>();

    public GameSimulator(int numPlayers, int numRounds) {
        this.gameState = new GameState(numPlayers);
        this.numOfRounds = numRounds;
        this.players = Arrays.asList(
                new Player("1", "Player1", null, gameState),
                new Player("2", "Player2", null, gameState),
                new Player("3", "Player3", null, gameState),
                new Player("4", "Player4", null, gameState)
        );
    }

    private void playRound(int round) {
        System.out.println("Starting round " + (round + 1));
        for(Player player : players) {
            Thread thread = new Thread(player);
            thread.start();
            threads.add(thread);
        }

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startGame() {
        for(int i = 0; i < numOfRounds; i++) {
            playRound(i);
        }
    }

    public static void main(String[] args) {
        GameSimulator simulator = new GameSimulator(4, 1);
        simulator.startGame();
        System.out.println("Game over");
    }
}
