package com.luismichu.pixelrun;

public class Score {
    String name;
    int score;

    Score(){}

    Score(int score){
        this.score = score;
    }

    Score(String name, int score){
        this.name = name;
        this.score = score;
    }
}
