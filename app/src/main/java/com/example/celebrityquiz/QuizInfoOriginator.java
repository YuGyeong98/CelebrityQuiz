package com.example.celebrityquiz;

import java.io.Serializable;

public class QuizInfoOriginator {

    public static String DOMAIN_ANIMAL = "Animal";
    public static String DOMAIN_CELEBRITY = "Celebrity";
    public static String DOMAIN_SCIENCE = "Science";

    private String domain;
    private int level;
    private int seconds;
    private static QuizInfoOriginator quizInfoOriginator;
    boolean isDownloaded = false;

    private QuizInfoOriginator(){

    }

    public static QuizInfoOriginator getInstance(){
        if(quizInfoOriginator == null)
            quizInfoOriginator = new QuizInfoOriginator();
        return quizInfoOriginator;
    }

    public String getDomain() {
        return domain;
    }

    public int getLevel() {
        return level;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isDownloaded(){
        return isDownloaded;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        isDownloaded = true;
    }

    public void setLevel(int level) {
        this.level = level;
        isDownloaded = true;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
        isDownloaded = true;
    }
}

