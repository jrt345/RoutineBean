package com.example.routinebean.commands;

import com.example.routinebean.utils.Routine;

public class Memento {

    public Routine routine;

    public Memento(Routine routine) {
        this.routine = routine;
    }

    public Routine getSavedRoutine() {
        return routine;
    }
}
