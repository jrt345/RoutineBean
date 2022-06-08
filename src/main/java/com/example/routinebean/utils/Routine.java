package com.example.routinebean.utils;

import java.io.Serializable;

public class Routine implements Serializable {

    private String title;
    private String[][] tasks;
    private String[][] backgroundColors;

    public Routine() {
        this("Routine", new String[24][7], new String[24][7]);
    }

    public Routine(String title, String[][] tasks, String[][] backgroundColors) {
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

    public String[][] getBackgroundColors() {
        return backgroundColors;
    }

    public void setBackgroundColors(String[][] backgroundColors) {
        this.backgroundColors = backgroundColors;
    }
}
