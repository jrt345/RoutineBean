package com.example.routinebean.data;

import com.example.routinebean.utils.AppUtils;

import java.io.*;

public class AppData {

    private static File serializedRoutine(String directory) {
        return new File(AppUtils.ROUTINES_DIRECTORY, new File(directory, "routine.dat").getPath());
    }

    public static void serialize(String directory, Routine routine) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(serializedRoutine(directory)); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(routine);
        }
    }

    public static Routine deserialize(String directory) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(serializedRoutine(directory)); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Routine) in.readObject();
        }
    }
}
