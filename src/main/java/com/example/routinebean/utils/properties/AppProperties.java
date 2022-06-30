package com.example.routinebean.utils.properties;

import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    private static Stage stage;
    private static double width;
    private static double height;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        AppProperties.stage = stage;
    }

    public static double getWidth() {
        return width;
    }

    public static void setWidth(double width) {
        AppProperties.width = width;
    }

    public static double getHeight() {
        return height;
    }

    public static void setHeight(double height) {
        AppProperties.height = height;
    }

    public static void loadDefaultProperties() throws IOException {
        width = 916;
        height = 639;
        write();
        load();
    }

    public static void write() throws IOException {
        Properties properties = new Properties();
        OutputStream os = new FileOutputStream(System.getProperty("user.dir").concat("\\RoutineBean.properties"));

        properties.setProperty("main-window-width", String.valueOf(width));
        properties.setProperty("main-window-height", String.valueOf(height));

        properties.store(os, null);
    }

    public static void load() throws IOException, NullPointerException, NumberFormatException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(System.getProperty("user.dir").concat("\\RoutineBean.properties"));
        properties.load(inputStream);

        width = Double.parseDouble(properties.getProperty("main-window-width"));
        height = Double.parseDouble(properties.getProperty("main-window-height"));

        stage.setWidth(width);
        stage.setHeight(height);
    }
}
