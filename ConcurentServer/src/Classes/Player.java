package Classes;


public class Player implements Runnable{

    private final int id;
    private final GameStateNew state;


    public Player(int id, GameStateNew state) {
        this.id = id;
        this.state = state;
    }

    @Override
    public void run()  {

        while (!state.isGameEnded()) {
            try {
                state.rollDice(this);
                long randomWait = Math.round(Math.random() * 1000);
                if(this.id != state.getCurrentPlayer()) {
                    Thread.sleep(randomWait/2);
                   
                }
                else{
                    Thread.sleep(randomWait/3);
                }
                state.tryToBuild(this, new Settlement(true));
    
            } catch (InterruptedException e) {
                System.out.println("Player " + this.id + " was interrupted");
            }
        }

    }

    public int getId() {
        return id;
    }
}
