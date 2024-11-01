
import Classes.GameStateNew;
import Classes.Player;

public class GameSimulator {
    public static void main(String[] args) {
       int numPlayers = 3;
        GameStateNew state = new GameStateNew(numPlayers);

        Thread[] playerThreads = new Thread[3];

        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(i, state);
            playerThreads[i] = new Thread(player);
            playerThreads[i].start();
        } 

       
    }
}