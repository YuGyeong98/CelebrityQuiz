package com.example.celebrityquiz;

import java.io.Serializable;

// Bridge class connecting MainActivity with QuizAdapter
// Serialize to extract objects from intent in SolutionActivity
public class Quiz implements Serializable {
    String question;
    String imageUrl;
    String one;
    String two;
    String three;
    String four;
    int correctAnswer;
    int userAnswer;

    public Quiz(){

    }

    Quiz(String question, String imageUrl, String one, String two,
         String three, String four, int correctAnswer, int userAnswer) {
        this.question = question;
        this.imageUrl = imageUrl;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public int getUserAnswer() {
        return userAnswer;
    }

    public String getFour() {
        return four;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOne() {
        return one;
    }

    public String getQuestion() {
        return question;
    }

    public String getThree() {
        return three;
    }

    public String getTwo() {
        return two;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setFour(String four) {
        this.four = four;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }
}