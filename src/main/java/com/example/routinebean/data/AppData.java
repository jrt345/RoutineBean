package com.example.routinebean.data;

import com.example.routinebean.utils.AppUtils;

import java.io.*;

public class AppData {

    public static void serialize(String folder, Routine routine) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(AppUtils.createRoutineDataFile(folder, "routine.dat"));
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(routine);
        out.close();
        fileOut.close();
    }

    public static Routine deserialize(String folder) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(AppUtils.createRoutineDataFile(folder, "routine.dat"));
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Routine routine = (Routine) in.readObject();
        in.close();
        fileIn.close();

        return routine;
    }
}
