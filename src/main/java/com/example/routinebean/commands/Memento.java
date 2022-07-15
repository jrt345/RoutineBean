package com.example.routinebean.commands;

import com.example.routinebean.data.Routine;

public record Memento(Routine routine) {

    public Routine getSavedRoutine() {
        return routine;
    }
}
