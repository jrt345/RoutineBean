package com.example.routinebean.utils;

import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    private static double mainWindowWidth;
    private static double mainWindowHeight;

    private static double routineWindowWidth;
    private static double routineWindowHeight;

    public static void setMainWindowSize(double width, double height) {
        mainWindowWidth = width;
        mainWindowHeight = height;
    }

    public static void setRoutineWindowSize(double width, double height) {
        routineWindowWidth = width;
        routineWindowHeight = height;
    }

    public static void writeProperties() throws IOException {
        Properties properties = new Properties();
        OutputStream os = new FileOutputStream("RoutineBean.properties");

        properties.setProperty("main-window-width", String.valueOf(mainWindowWidth));
        properties.setProperty("main-window-height", String.valueOf(mainWindowHeight));
        properties.setProperty("routine-window-width", String.valueOf(routineWindowWidth));
        properties.setProperty("routine-window-height", String.valueOf(routineWindowHeight));

        properties.store(os, null);
    }

    public static Properties readProperties() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("RoutineBean.properties");
        properties.load(inputStream);
        return properties;
    }

    public static void saveProperties(Stage stage, boolean isMainWindow) {
        double width = stage.getWidth();
        double height = stage.getHeight();

        if (isMainWindow) {
            AppProperties.setMainWindowSize(width, height);
        } else {
            AppProperties.setRoutineWindowSize(width, height);
        }

        try {
            AppProperties.writeProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setWindowSizeFromProperties(Stage stage, boolean isMainWindow) {
        Properties properties = null;

        try {
            properties = AppProperties.readProperties();
        } catch (IOException e) {
            AppProperties.setMainWindowSize(900, 600);
            AppProperties.setRoutineWindowSize(900, 600);
            try {
                AppProperties.writeProperties();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        String windowType;
        if (isMainWindow) {
            windowType = "main";
        } else {
            windowType = "routine";
        }

        if (properties != null) {
            double width = Double.parseDouble(properties.getProperty(windowType + "-window-width"));
            double height = Double.parseDouble(properties.getProperty(windowType + "-window-height"));
            stage.setWidth(width);
            stage.setHeight(height);

            if (isMainWindow) {
                AppProperties.setMainWindowSize(width, height);
            } else {
                AppProperties.setRoutineWindowSize(width, height);
            }
        }
    }
}
