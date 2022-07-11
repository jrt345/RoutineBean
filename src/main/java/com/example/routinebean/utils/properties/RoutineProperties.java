package com.example.routinebean.utils.properties;

import com.example.routinebean.utils.AppData;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class RoutineProperties {

    private String directory;
    private double width;
    private double height;

    public RoutineProperties(String directory) {
        this.directory = directory;
        this.width = 916.0;
        this.height = 639.0;
    }

    public static RoutineProperties load(String directory) throws IOException, NullPointerException, NumberFormatException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(AppData.ROUTINE_DIRECTORY.concat(directory + "\\Routine.properties"));
        properties.load(inputStream);

        RoutineProperties routineProperties = new RoutineProperties(directory);
        routineProperties.width = Double.parseDouble(properties.getProperty("routine-window-width"));
        routineProperties.height = Double.parseDouble(properties.getProperty("routine-window-height"));

        inputStream.close();

        return routineProperties;
    }

    public static void write(RoutineProperties routineProperties) throws IOException {
        Properties properties = new Properties();
        OutputStream outputStream = new FileOutputStream(AppData.ROUTINE_DIRECTORY.concat(routineProperties.directory) + "\\Routine.properties");

        properties.setProperty("routine-window-width", String.valueOf(routineProperties.width));
        properties.setProperty("routine-window-height", String.valueOf(routineProperties.height));

        properties.store(outputStream, null);
        outputStream.close();
    }

    public void setStageSize(Stage stage) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
