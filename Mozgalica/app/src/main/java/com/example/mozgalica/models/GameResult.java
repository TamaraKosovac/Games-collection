package com.example.mozgalica.models;

public class GameResult {
    private int id;
    private String username;
    private String game;
    private String result;
    private int score;
    private String timestamp;

    public GameResult(int id, String username, String game, String result, int score, String timestamp) {
        this.id = id;
        this.username = username;
        this.game = game;
        this.result = result;
        this.score = score;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getGame() {
        return game;
    }

    public String getResult() {
        return result;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
