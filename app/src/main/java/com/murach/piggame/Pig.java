package com.murach.piggame;

import com.murach.piggame.Dice;
import com.murach.piggame.Player;

public class Pig {
    public Player player1;
    public Player player2;
    private Dice die;
    public Integer whoseTurn;
    private Integer runningTotal;
    private Integer lastRolledNumber;
    private Boolean player1Win;

    public Pig(String player1Name, String player2Name, int dieSides) {
        this.player1 = new Player(player1Name);
        this.player2 = new Player(player2Name);
        this.die = new Dice(dieSides);
        this.whoseTurn = 1;
    }

    public String getPlayerName(Integer playerNumber){
        if(playerNumber == 1) {
            return player1.getName();
        } else {
            return player2.getName();
        }
    }

    public int RollAndCalculate() {
        int rolled = this.rollDie();
        if (rolled != 6) {
            return rolled;
        } else {
            resetRunningTotal();
            return rolled;
        }
    }

    private int rollDie() {
        return this.die.Roll();
    }

    private void resetRunningTotal() {
        this.runningTotal = 0;
    }

    public void SetTurn() {
        if (whoseTurn == 1) {
            whoseTurn = 2;
        } else {
            whoseTurn = 1;
        }
    }
}
