package com.example.routinebean.utils;

import javafx.scene.paint.Color;

import java.io.Serial;
import java.io.Serializable;

public class Routine implements Serializable {

    @Serial
    private static final long serialVersionUID = 2128630904469519447L;

    private String title;
    private String[][] tasks;
    private String[][] backgroundColors;

    private static String[][] initialColor(){
        String[][] colorsArray = new String[24][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                colorsArray[j][i] = ColorUtils.colorToRGBA(Color.WHITE);
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
