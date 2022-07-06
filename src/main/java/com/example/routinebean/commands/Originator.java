package com.example.routinebean.commands;

import com.example.routinebean.utils.Routine;

public class Originator {

    private Routine routine;

    public void set(Routine routine) {
        this.routine = routine;
    }

    public Memento storeInMemento() {
        return new Memento(routine);
    }

    public Routine restoreFromMemento(Memento memento) {
        routine = memento.getSavedRoutine();
        return routine;
    }
}
