package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.data.AppData;
import com.example.routinebean.data.Routine;
import com.example.routinebean.properties.RoutineProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class AppUtils {

    public static final String STYLESHEET = Objects.requireNonNull(App.class.getResource("stylesheet.css")).toExternalForm();

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

    public static RoutineProperties loadRoutineProperties(String directory) {
        try {
            return RoutineProperties.load(directory);
        } catch (IOException | NullPointerException | NumberFormatException e) {
            RoutineProperties routineProperties = new RoutineProperties(directory);

            try {
                RoutineProperties.write(routineProperties);
                return RoutineProperties.load(directory);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void writeRoutineProperties(RoutineProperties properties, Stage stage) {
        try {
            properties.setSize(stage.getWidth(), stage.getHeight());
            RoutineProperties.write(properties);
        } catch (IOException e) {
            RoutineProperties routineProperties = new RoutineProperties(properties.getDirectory());
            routineProperties.setSize(stage.getWidth(), stage.getHeight());

            try {
                RoutineProperties.write(routineProperties);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void openDirectory(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }

    public static void openUrlInBrowser(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    }

    public static void openAboutBox() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("aboutBox.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setScene(scene);
        stage.setMinWidth(516);
        stage.setMinHeight(389);
        stage.setResizable(false);
        stage.getIcons().add(AppUtils.ICON);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }
}
