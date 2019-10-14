package com.murach.piggame;

public class Player {
    public Integer score;
    public String name;

    public Player(String playerName) {
        this.name = playerName;
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String playerName) {
        this.name = playerName;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int newScore) {
        this.score += newScore;
    }

    public void clearScore() {
        this.score = 0;
    }
}
