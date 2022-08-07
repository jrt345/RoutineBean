package com.example.routinebean.properties;

import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class AppProperties {

    private static final File PROPERTIES = new File("RoutineBean.properties");

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

    public static Optional<AppProperties> load() {
        try (InputStream inputStream = new FileInputStream(PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            double width = Double.parseDouble(properties.getProperty("main-window-width"));
            double height = Double.parseDouble(properties.getProperty("main-window-height"));
            boolean checkForUpdates = Boolean.parseBoolean(properties.getProperty("check-for-update"));

            return Optional.of(new AppProperties(width, height, checkForUpdates));
        } catch (IOException | NullPointerException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static void write(AppProperties appProperties) throws IOException {
        if (appProperties == null) {
            throw new NullPointerException();
        }

        try (OutputStream os = new FileOutputStream(PROPERTIES)) {
            Properties properties = new Properties();
            properties.setProperty("main-window-width", String.valueOf(appProperties.width));
            properties.setProperty("main-window-height", String.valueOf(appProperties.height));
            properties.setProperty("check-for-update", String.valueOf(appProperties.checkForUpdate));

            properties.store(os, null);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppProperties that = (AppProperties) o;

        if (Double.compare(that.width, width) != 0) return false;
        if (Double.compare(that.height, height) != 0) return false;
        return checkForUpdate == that.checkForUpdate;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(width);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (checkForUpdate ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "width=" + width +
                ", height=" + height +
                ", checkForUpdate=" + checkForUpdate +
                '}';
    }
}
