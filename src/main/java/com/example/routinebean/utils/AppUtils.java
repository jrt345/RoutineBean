package com.example.routinebean.utils;

import com.example.routinebean.App;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class AppUtils {

    public static final Image ICON = new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/routinebean-logo.png")));

    private static String getDuplicateFolderName(String name) {
        int index = 0;
        String duplicateFolderName = name;
        File file = new File(AppData.ROUTINE_DIRECTORY.concat(name));

        while (file.exists()){
            index++;
            duplicateFolderName = name + " (" + index + ")";

            file = new File(AppData.ROUTINE_DIRECTORY.concat(duplicateFolderName));
        }

        return duplicateFolderName;
    }

    public static String filterFolderName(String name) {
        String filteredFolderName = name;

        filteredFolderName = filteredFolderName.replaceAll("[<>:\"/\\\\|?.*]", "_");

        filteredFolderName = filteredFolderName.trim();

        if (filteredFolderName.equals("")){
            filteredFolderName = "Routine";
        }

        if (Pattern.matches("CON$|PRN$|AUX$|NUL$|COM1$|COM2$|COM3$|COM4$|COM5$|COM6$|COM7$|COM8$|COM9$|LPT1$|LPT2$|LPT3$|LPT4$|LPT5$|LPT6$|LPT7$|LPT8$|LPT9$", filteredFolderName)) {
            filteredFolderName = "_" + filteredFolderName + "_";
        }

        if (new File(AppData.ROUTINE_DIRECTORY.concat(filteredFolderName)).exists()){
            filteredFolderName = getDuplicateFolderName(filteredFolderName);
        }

        return filteredFolderName;
    }

    public static boolean createNewRoutine(String title, Routine routine) {
        String filteredFolderName = filterFolderName(title);

        boolean isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).exists();

        if (!isRoutinesDirCreated) {
            isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        boolean isRoutineCreated = false;

        if (isRoutinesDirCreated) {
            isRoutineCreated = new File(AppData.ROUTINE_DIRECTORY.concat(filteredFolderName)).mkdirs();
        }

        if (isRoutineCreated) {
            try {
                AppData.serialize(filteredFolderName, routine);
            } catch (IOException e) {
                isRoutineCreated = false;
            }
        }

        return isRoutineCreated;
    }

    public static void openExplorer(String directory) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select," + AppData.ROUTINE_DIRECTORY.concat(directory) + "\\routine.dat");
    }
}
