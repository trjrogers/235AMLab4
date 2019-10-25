package com.murach.piggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Resources;
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

    private int dieNumber;
    private int pointsThisTurn;
    private boolean endGame = false;

    private Pig pig;

    private SharedPreferences savedValues;


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

        DisableEndTurnButton();
        DisableRollButton();

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
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
                dieNumber = 1;
                break;
            }
            case 2: {
                ref = R.drawable.die2;
                dieNumber = 2;
                break;
            }
            case 3: {
                ref = R.drawable.die3;
                dieNumber = 3;
                break;
            }
            case 4: {
                ref = R.drawable.die4;
                dieNumber = 4;
                break;
            }
            case 5: {
                ref = R.drawable.die5;
                dieNumber = 5;
                break;
            }
            case 6: {
                ref = R.drawable.die6;
                dieNumber = 6;
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
        dieNumber = -1;

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

    @Override
    public void onPause() {
        // Save instance variables
        Editor editor = savedValues.edit();
        editor.putString("player1Label", String.valueOf(player1Label.getText()));
        editor.putInt("player1Score", pig.player1.score);
        editor.putString("player2Label", String.valueOf(player2Label.getText()));
        editor.putInt("player2Score", pig.player2.score);
        editor.putInt("currentTurnPoints", pointsThisTurn);
        editor.putInt("whoseTurn", pig.whoseTurn);
        editor.putBoolean("gameOver", endGame);
        editor.putInt("dieNumber", dieNumber);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        Pig pig = new Pig("Human", "Computer", 6);

        player1Label.setText(savedValues.getString("player1Label", ""));
        pig.player1.score = savedValues.getInt("player1Score", 0);
        player2Label.setText(savedValues.getString("player2Label", ""));
        pig.player2.score = savedValues.getInt("player2Score", 0);
        pointsThisTurn = savedValues.getInt("currentTurnPoints", 0);
        pig.whoseTurn = savedValues.getInt("whoseTurn", 1);
        endGame = savedValues.getBoolean("gameOver", false);
        UpdateImage(savedValues.getInt("dieNumber", 1));
    }
}
