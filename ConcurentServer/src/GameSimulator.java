import Classes.GameState;
import Classes.Player;
import Enums.ActionsType;

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
            currentPlayer.takeAction(gameState, ActionsType.ROLL_DICE);
            switch (currentPlayer.getId()) {
                case "1":
                    currentPlayer.takeAction(gameState, ActionsType.INITIATE_TRADE);
                    break;
                case "2":
               
                    currentPlayer.takeAction(gameState, ActionsType.PLACE_ROAD);
                    currentPlayer.takeAction(gameState, ActionsType.PLACE_SETTLEMENT);
                    break;
                case "3":
                    currentPlayer.takeAction(gameState, ActionsType.ACCEPT_TRADE);
                    currentPlayer.takeAction(gameState, ActionsType.PLACE_SETTLEMENT);
                    break;
                case "4":
                    currentPlayer.takeAction(gameState, ActionsType.DECLINE_TRADE);
                    currentPlayer.takeAction(gameState, ActionsType.PLACE_CITY);
                    break;
            
                default:

                    break;
            }
        }
                
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
        GameSimulator simulator = new GameSimulator(4, 2);
        simulator.start();
    }
}
