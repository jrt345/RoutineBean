package com.example.routinebean.commands;

import java.util.ArrayList;

public class Caretaker {

    ArrayList<Memento> savedRoutines = new ArrayList<>();

    public void addMemento(Memento memento) {
        savedRoutines.add(memento);
    }

    public Memento getMemento(int index) {
        return savedRoutines.get(index);
    }

    public void clearMemento(int index){
        savedRoutines.subList(index, savedRoutines.size()).clear();
    }
}
