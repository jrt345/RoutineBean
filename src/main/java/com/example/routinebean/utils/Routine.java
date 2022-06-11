package com.example.routinebean.utils;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Routine implements Serializable {

    private String title;
    private String[][] tasks;
    private Color[][] backgroundColors;

    public Routine() {
        this("Routine", new String[24][7], new Color[24][7]);
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
