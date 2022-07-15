package com.example.routinebean.controllers;

import com.example.routinebean.data.Routine;
import javafx.scene.control.Button;

public class RoutineLoader {

    private String directory;
    private Routine routine;
    private Button button;

    RoutineLoader(String directory, Routine routine, Button button) {
        this.directory = directory;
        this.routine = routine;
        this.button = button;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutineLoader loader = (RoutineLoader) o;

        if (!directory.equals(loader.directory)) return false;
        if (!routine.equals(loader.routine)) return false;
        return button.equals(loader.button);
    }

    @Override
    public int hashCode() {
        int result = directory.hashCode();
        result = 31 * result + routine.hashCode();
        result = 31 * result + button.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RoutineLoader{" +
                "directory='" + directory + '\'' +
                ", routine=" + routine +
                ", button=" + button +
                '}';
    }
}
