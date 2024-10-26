import Classes.GameState;
import Classes.Player;

public class GameSimulator {
    public static void main(String[] args) {
        GameState state = new GameState();
        Player player1 = new Player(0, state);
        Player player2 = new Player(1, state);

        state.addPlayer(0, player1);
        state.addPlayer(1, player2);

        Thread thread1 = new Thread(player1);
        Thread thread2 = new Thread(player2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Game interrupted: " + e.getMessage());
        }
    }
}