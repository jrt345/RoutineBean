package com.example.routinebean.utils.properties;

import com.example.routinebean.utils.AppData;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class RoutineProperties {

    private static Stage stage;
    private static double width;
    private static double height;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        RoutineProperties.stage = stage;
    }

    public static double getWidth() {
        return width;
    }

    public static void setWidth(double width) {
        RoutineProperties.width = width;
    }

    public static double getHeight() {
        return height;
    }

    public static void setHeight(double height) {
        RoutineProperties.height = height;
    }

    public static void loadDefaultProperties(String folder) throws IOException {
        width = 916;
        height = 639;
        write(folder);
        load(folder);
    }

    public static void write(String folder) throws IOException {
        Properties properties = new Properties();
        OutputStream outputStream = new FileOutputStream(AppData.ROUTINE_DIRECTORY.concat(folder + "\\Routine.properties"));

        properties.setProperty("routine-window-width", String.valueOf(width));
        properties.setProperty("routine-window-height", String.valueOf(height));

        properties.store(outputStream, null);
        outputStream.close();
    }

    public static void load(String folder) throws IOException, NullPointerException, NumberFormatException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(AppData.ROUTINE_DIRECTORY.concat(folder + "\\Routine.properties"));

        properties.load(inputStream);
        inputStream.close();

        width = Double.parseDouble(properties.getProperty("routine-window-width"));
        height = Double.parseDouble(properties.getProperty("routine-window-height"));

        stage.setWidth(width);
        stage.setHeight(height);

    }
}
