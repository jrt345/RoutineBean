package com.example.routinebean.data;

import com.example.routinebean.utils.ColorUtils;
import javafx.scene.paint.Color;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Routine routine = (Routine) o;

        if (!Objects.equals(title, routine.title)) return false;
        if (!Arrays.deepEquals(tasks, routine.tasks)) return false;
        return Arrays.deepEquals(backgroundColors, routine.backgroundColors);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + Arrays.deepHashCode(tasks);
        result = 31 * result + Arrays.deepHashCode(backgroundColors);
        return result;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "title='" + title + '\'' +
                ", tasks=" + Arrays.toString(tasks) +
                ", backgroundColors=" + Arrays.toString(backgroundColors) +
                '}';
    }
}
