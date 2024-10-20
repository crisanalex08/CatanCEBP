import Classes.GameState;
import Classes.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameSimulator {
    private GameState gameState;
    private ExecutorService executorService;

    public GameSimulator(int numPlayers, int numRounds) {
        this.gameState = new GameState(numPlayers);
        this.executorService = Executors.newFixedThreadPool(numPlayers);

        for (int i = 0; i < numRounds; i++) {
            final int round = i;
            executorService.submit(() -> playRound(round));
        }
    }

    private void playRound(int round) {
        System.out.println("Starting round " + (round + 1));
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player currentPlayer = gameState.getCurrentPlayer();
            currentPlayer.takeAction(gameState);
            gameState.nextTurn();
        }
        System.out.println("Finished round " + (round + 1));
    }

    public void start() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Game simulation completed");
    }

    public static void main(String[] args) {
        GameSimulator simulator = new GameSimulator(4, 10);
        simulator.start();
    }
}
