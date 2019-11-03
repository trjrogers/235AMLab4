package com.murach.piggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
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
    private boolean player1Turn = true;

    private Pig pig;

    private SharedPreferences savedValues;
    private SharedPreferences prefs;
    private boolean ai = false;
    private int maxTurnPoints;
    private int maxRolls;

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

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        DisableEndTurnButton();
        DisableRollButton();

        Pig pig = new Pig("Human", "Computer", 6);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pig_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menu_about:
                Toast.makeText(this, "About", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Roll() {
        // disable rollButton
        // roll die
        // update image
        // update running score for this turn

        // if ai is enabled and player 2 turn
        //// get ai max turn points
        //// roll until either a 1 is rolled or their turn points are >= max turn points setting

        ai = prefs.getBoolean("pref_ai", false);

        if (ai && pig.whoseTurn == 2) {
            maxTurnPoints = Integer.parseInt(prefs.getString("pref_max_score", "100"));
            maxRolls = Integer.parseInt(prefs.getString("pref_roll_number", "1"));
            int lastRolled = 0;
            int timesRolled = 0;
            while ((pointsThisTurn <= maxTurnPoints) && (lastRolled != 1) && timesRolled < maxRolls){
                DisableRollButton();
                int result = this.pig.RollAndCalculate();
                lastRolled = result;
                UpdateImage(result);
                timesRolled++;

                Toast.makeText(getApplicationContext(), "Rolled " + result, Toast.LENGTH_SHORT);

                if (result != 1) {
                    UpdatePoints(result);
                    EnableRollButton();
                } else {
                    pointsThisTurn = 0;
                    pointsThisTurnTV.setText(String.valueOf(pointsThisTurn));
                }
            }
            DisableRollButton();
        } else {
            DisableRollButton();
            int result = this.pig.RollAndCalculate();
            UpdateImage(result);
            Toast.makeText(getApplicationContext(), "Rolled " + result, Toast.LENGTH_SHORT);

            if (result != 1) {
                UpdatePoints(result);
                EnableRollButton();
            } else {
                pointsThisTurn = 0;
                pointsThisTurnTV.setText(String.valueOf(pointsThisTurn));
            }
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
        try {
            Drawable drawable = getResources().getDrawable(ref);
            this.dieImage.setImageDrawable(drawable);
        } catch (Exception e) {
            ref = R.drawable.die1;
            Drawable drawable = getResources().getDrawable(ref);
            this.dieImage.setImageDrawable(drawable);
        }
    }

    private void SwitchTurns() {
        if (pig.whoseTurn == 1) {
            turnLabel.setText(player2Label.getText() + "'s turn");
            player1Turn = false;
        } else {
            turnLabel.setText(player1Label.getText() + "'s turn");
            player1Turn = true;
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
        endGame = false;
        pig = new Pig(p1, p2, 6);
        pig.whoseTurn = 1;

        turnLabel.setText(String.valueOf(player1EditText.getText()) + "'s turn");
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
        editor.putInt("player1Score", Integer.valueOf(String.valueOf(player1ScoreTV.getText())));
        editor.putString("player2Label", String.valueOf(player2Label.getText()));
        editor.putInt("player2Score", Integer.valueOf(String.valueOf(player2ScoreTV.getText())));
        editor.putInt("currentTurnPoints", pointsThisTurn);
        editor.putBoolean("whoseTurn", player1Turn);
        editor.putBoolean("gameOver", endGame);
        editor.putInt("dieNumber", dieNumber);
        editor.putString("turnLabel", String.valueOf(turnLabel.getText()));
        editor.commit();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        Pig pig = new Pig("Human", "Computer", 6);

        player1Label.setText(savedValues.getString("player1Label", ""));
        pig.player1.name = String.valueOf(player1Label.getText());
        pig.player1.score = savedValues.getInt("player1Score", 0);
        player1ScoreTV.setText(pig.player1.score.toString());
        player2Label.setText(savedValues.getString("player2Label", ""));
        pig.player2.name = String.valueOf(player2Label.getText());
        pig.player2.score = savedValues.getInt("player2Score", 0);
        player2ScoreTV.setText(pig.player2.score.toString());
        pointsThisTurn = savedValues.getInt("currentTurnPoints", 0);
        pointsThisTurnTV.setText(String.valueOf(pointsThisTurn));
        player1Turn = savedValues.getBoolean("whoseTurn", true);
        turnLabel.setText(savedValues.getString("turnLabel", pig.player1.name + "\'s turn"));
        if (player1Turn) {
            pig.whoseTurn = 1;
        } else {
            pig.whoseTurn = 2;
        }
        endGame = savedValues.getBoolean("gameOver", false);
        UpdateImage(savedValues.getInt("dieNumber", 1));

        ai = prefs.getBoolean("pref_ai", false);
        maxTurnPoints = Integer.parseInt(prefs.getString("pref_max_score", "100"));
    }
}
