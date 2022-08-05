package com.example.routinebean.properties;

import com.example.routinebean.utils.AppUtils;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class RoutineProperties {

    private String directory;
    private double width;
    private double height;

    public RoutineProperties(String directory) {
        this.directory = directory;
        this.width = 1236.0;
        this.height = 859.0;
    }

    private static File propertiesFile(String directory) {
        return new File(AppUtils.ROUTINES_DIRECTORY, new File(directory, "Routine.properties").getPath());
    }

    public static Optional<RoutineProperties> load(String directory) {
        try (InputStream inputStream = new FileInputStream(propertiesFile(directory))) {
            Properties properties = new Properties();
            properties.load(inputStream);

            RoutineProperties routineProperties = new RoutineProperties(directory);
            routineProperties.width = Double.parseDouble(properties.getProperty("routine-window-width"));
            routineProperties.height = Double.parseDouble(properties.getProperty("routine-window-height"));

            return Optional.of(routineProperties);
        } catch (IOException | NullPointerException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static void write(RoutineProperties routineProperties) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(propertiesFile(routineProperties.directory))) {
            Properties properties = new Properties();
            properties.setProperty("routine-window-width", String.valueOf(routineProperties.width));
            properties.setProperty("routine-window-height", String.valueOf(routineProperties.height));

            properties.store(outputStream, null);
        }
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
