package com.example.routinebean.utils;

import java.io.*;

public class AppData {

    public static final String userDataDir = System.getProperty("user.dir").concat("\\routines\\");

    public static void serialize(String folder, Routine routine) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(userDataDir.concat(folder + "\\routine.dat"));
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(routine);
        out.close();
        fileOut.close();
    }

    public static Routine deserialize(String folder) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(userDataDir.concat(folder + "\\routine.dat"));
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Routine routine = (Routine) in.readObject();
        in.close();
        fileIn.close();

        return routine;
    }
}
