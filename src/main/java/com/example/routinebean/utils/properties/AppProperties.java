package com.example.routinebean.utils.properties;

import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    private double width;
    private double height;
    private boolean checkForUpdate;

    public AppProperties() {
        this.width = 916;
        this.height = 639;
        this.checkForUpdate = true;
    }

    public AppProperties(double width, double height, boolean checkForUpdate) {
        this.width = width;
        this.height = height;
        this.checkForUpdate = checkForUpdate;
    }

    public static AppProperties load() throws IOException, NullPointerException, NumberFormatException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(System.getProperty("user.dir").concat("\\RoutineBean.properties"));
        properties.load(inputStream);

        double width = Double.parseDouble(properties.getProperty("main-window-width"));
        double height = Double.parseDouble(properties.getProperty("main-window-height"));
        boolean checkForUpdates = Boolean.parseBoolean(properties.getProperty("check-for-update"));

        inputStream.close();

        return new AppProperties(width, height, checkForUpdates);
    }

    public static void write(AppProperties appProperties) throws IOException {
        Properties properties = new Properties();
        OutputStream os = new FileOutputStream(System.getProperty("user.dir").concat("\\RoutineBean.properties"));

        properties.setProperty("main-window-width", String.valueOf(appProperties.width));
        properties.setProperty("main-window-height", String.valueOf(appProperties.height));
        properties.setProperty("check-for-update", String.valueOf(appProperties.checkForUpdate));

        properties.store(os, null);
    }

    public void setStageSize(Stage stage) {
        stage.setWidth(width);
        stage.setHeight(height);
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

    public boolean isCheckForUpdate() {
        return checkForUpdate;
    }

    public void setCheckForUpdate(boolean checkForUpdate) {
        this.checkForUpdate = checkForUpdate;
    }
}
