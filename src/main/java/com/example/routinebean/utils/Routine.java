package com.example.routinebean.utils;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Routine implements Serializable {

    private String title;
    private String[][] tasks;
    private Color[][] backgroundColors;

    private static Color[][] initialColor(){
        Color[][] colorsArray = new Color[24][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                colorsArray[j][i] = Color.WHITE;
            }
        }
        return colorsArray;
    }

    public Routine() {
        this("Routine", new String[24][7], initialColor());
    }

    public Routine(String title) {
        this(title, new String[24][7], initialColor());
    }

    public Routine(String title, String[][] tasks, Color[][] backgroundColors) {
        this.title = title;
        this.tasks = tasks;
        this.backgroundColors = backgroundColors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[][] getTasks() {
        return tasks;
    }

    public void setTasks(String[][] tasks) {
        this.tasks = tasks;
    }

    public Color[][] getBackgroundColors() {
        return backgroundColors;
    }

    public void setBackgroundColors(Color[][] backgroundColors) {
        this.backgroundColors = backgroundColors;
    }
}
