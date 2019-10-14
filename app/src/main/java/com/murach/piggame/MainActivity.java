package com.murach.piggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends AppCompatActivity {

    // UI Instance variables
    private TextView player1Label;
    private TextView player2Label;
    private EditText player1EditText;
    private EditText player2EditText;
    private TextView player1ScoreTV;
    private TextView player2ScoreTV;
    private TextView turnLabel;
    private ImageView dieImage;
    private TextView pointsThisTurnTV;
    private Button rollButton;
    private Button endTurnButton;
    private Button newGameButton;

    private int pointsThisTurn;
    private boolean endGame = false;

    private Pig pig;

    private SharedPreferences savedValues;
    boolean stateRecovered = false;
    boolean stateSaved = false;

    // need to save: player1Username, player1Score, player2Username, player2Score, currentTurnPoints, whoseTurn, game over?, die image
    private String PLAYER1_USERNAME;
    private Integer PLAYER1_SCORE;
    private String PLAYER2_USERNAME;
    private Integer PLAYER2_SCORE;
    private Integer CURRENT_TURN_POINTS;
    private Integer WHOSE_TURN;
    private boolean GAME_OVER;
    private String CURRENT_DIE_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.player1Label = findViewById(R.id.player1Label);
        this.player2Label = findViewById(R.id.player2Label);
        this.player1EditText = findViewById(R.id.player1EditText);
        this.player2EditText = findViewById(R.id.player2EditText);
        this.player1ScoreTV = findViewById(R.id.player1ScoreTV);
        this.player2ScoreTV = findViewById(R.id.player2ScoreTV);
        this.turnLabel = findViewById(R.id.turnLabel);
        this.dieImage = findViewById(R.id.dieImage);
        this.pointsThisTurnTV = findViewById(R.id.pointsThisTurnTV);
        this.rollButton = findViewById(R.id.rollButton);
        this.endTurnButton = findViewById(R.id.endTurnButton);
        this.newGameButton = findViewById(R.id.newGameButton);

        this.rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Roll();
            }
        });

        this.endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndTurn();
            }
        });

        this.newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGame();
            }
        });

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        DisableEndTurnButton();
        DisableRollButton();
    }

    private void Roll() {
        // disable rollButton
        // roll die
        // update image
        // update running score for this turn
        DisableRollButton();
        int result = this.pig.RollAndCalculate();
        UpdateImage(result);

        if (result != 1) {
            UpdatePoints(result);
            EnableRollButton();
        } else {
            pointsThisTurn = 0;
            pointsThisTurnTV.setText(String.valueOf(pointsThisTurn));
        }
    }

    private void DisableRollButton() {
        this.rollButton.setEnabled(false);
    }

    private void DisableEndTurnButton() {
        this.endTurnButton.setEnabled(false);
    }

    private void EnableEndTurnButton() {
        this.endTurnButton.setEnabled(true);
    }

    private void EnableRollButton() {
        this.rollButton.setEnabled(true);
    }

    private void UpdatePoints(int points) {
        pointsThisTurn += points;
        this.pointsThisTurnTV.setText(String.valueOf(pointsThisTurn));
    }

    private void UpdateImage(int rolled) {
        int ref = 0;
        switch(rolled){
            case 1: {
                ref = R.drawable.die1;
                break;
            }
            case 2: {
                ref = R.drawable.die2;
                break;
            }
            case 3: {
                ref = R.drawable.die3;
                break;
            }
            case 4: {
                ref = R.drawable.die4;
                break;
            }
            case 5: {
                ref = R.drawable.die5;
                break;
            }
            case 6: {
                ref = R.drawable.die6;
                break;
            }default: {
                ref = -1;
                break;
            }
        }
        Drawable drawable = getResources().getDrawable(ref);
        this.dieImage.setImageDrawable(drawable);
    }

    private void SwitchTurns() {
        if (pig.whoseTurn == 1) {
            turnLabel.setText(player2Label.getText() + "'s turn");
        } else {
            turnLabel.setText(player1Label.getText() + "'s turn");
        }
        this.pig.SetTurn();
        pointsThisTurn = 0;
        UpdatePoints(pointsThisTurn);
        EnableRollButton();
    }

    private void EndTurn() {
        if (!endGame) {
            if (pig.whoseTurn == 1) {
                DisableRollButton();
                pig.player1.score += pointsThisTurn;
                player1ScoreTV.setText(String.valueOf(pig.player1.score));
                CheckForWinner();
                SwitchTurns();
                EnableRollButton();
            } else {
                DisableRollButton();
                pig.player2.score += pointsThisTurn;
                player2ScoreTV.setText(String.valueOf(pig.player2.score));
                CheckForWinner();
                SwitchTurns();
                EnableRollButton();
            }
        } else {
            if (pig.player1.score > pig.player2.score) {
                Toast.makeText(getApplicationContext(),player1Label.getText() + " wins!",Toast.LENGTH_LONG).show();
            } else if (pig.player2.score > pig.player1.score) {
                Toast.makeText(getApplicationContext(),player2Label.getText() + " wins!",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"Tie!",Toast.LENGTH_LONG).show();
            }
            DisableRollButton();
            DisableEndTurnButton();
        }
    }

    private void NewGame() {
        player1ScoreTV.setText("0");
        player2ScoreTV.setText("0");
        pointsThisTurnTV.setText("0");
        pointsThisTurn = 0;

        String p1 = String.valueOf(player1EditText);
        String p2 = String.valueOf(player2EditText);

        if (player1Label.getText() != "") {
            player1Label.setText(player1EditText.getText());
        } else {
            player1Label.setText("Player 1");
        }

        if (player2Label.getText() != "") {
            player2Label.setText(player2EditText.getText());
        } else {
            player2Label.setText("Player 2");
        }

        pig = new Pig(p1, p2, 6);
        pig.whoseTurn = 1;
        EnableRollButton();
        EnableEndTurnButton();
    }

    private void CheckForWinner() {
        if (pig.player1.score >= 100) {
            endGame = true;
        } else if (pig.player2.score >= 100) {
            endGame = true;
        } else {
            endGame = false;
        }
    }

//    @Override
//    public void onPause() {
//        Editor editor = savedValues.edit();
//        PLAYER1_USERNAME = String.valueOf(player1Label.getText());
//        PLAYER1_SCORE = pig.player1.score;
//        PLAYER2_USERNAME = String.valueOf(player2Label.getText());
//        PLAYER2_SCORE = pig.player2.score;
//        CURRENT_TURN_POINTS = pointsThisTurn;
//        WHOSE_TURN = pig.whoseTurn;
//        GAME_OVER = endGame;
//        CURRENT_DIE_IMAGE = dieImage.toString();
//
//        editor.putString("PLAYER1_USERNAME", PLAYER1_USERNAME);
//        editor.putInt("PLAYER1_SCORE", PLAYER1_SCORE);
//        editor.putString("PLAYER2_USERNAME", PLAYER2_USERNAME);
//        editor.putInt("PLAYER2_SCORE", PLAYER2_SCORE);
//        editor.putInt("CURRENT_TURN_POINTS", CURRENT_TURN_POINTS);
//        editor.putInt("WHOSE_TURN", WHOSE_TURN);
//        editor.putBoolean("GAME_OVER", GAME_OVER);
//        editor.putString("CURRENT_DIE_IMAGE", CURRENT_DIE_IMAGE);
//        editor.commit();
//        super.onPause();
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Pig pig = new Pig(savedValues.getString("PLAYER1_USERNAME", "Human"), savedValues.getString("PLAYER2_USERNAME", "Computer"), 6);
////        player1Label.setText(savedValues.getString("PLAYER1_USERNAME", "Human"));
//        player1ScoreTV.setText(String.valueOf(savedValues.getInt("PLAYER1_SCORE", 0)));
////        player2Label.setText(savedValues.getString("PLAYER2_USERNAME", "Computer"));
//        player2ScoreTV.setText(String.valueOf(savedValues.getInt("PLAYER2_SCORE", 0)));
//        pointsThisTurnTV.setText(String.valueOf(savedValues.getInt("CURRENT_TURN_POINTS", 0)));
//        pointsThisTurn = savedValues.getInt("CURRENT_TURN_POINTS", 0);
//        pig.whoseTurn = savedValues.getInt("WHOSE_TURN", 1);
//        endGame = savedValues.getBoolean("GAME_OVER", false);
//
//        String image = savedValues.getString("CURRENT_DIE_IMAGE", "die1");
//        String sub = image.substring(3);
//        int ref = Integer.parseInt(sub);
//        UpdateImage(ref);
//    }
}

//     private String PLAYER1_USERNAME;
//    private Integer PLAYER1_SCORE;
//    private String PLAYER2_USERNAME;
//    private Integer PLAYER2_SCORE;
//    private Integer CURRENT_TURN_POINTS;
//    private Integer WHOSE_TURN;
//    private boolean GAME_OVER;
//    private String CURRENT_DIE_IMAGE;

