package com.example.routinebean;

import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.properties.AppProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    private static void writeProperties(AppProperties properties, Stage stage) {
        try {
            properties.setSize(stage.getWidth(), stage.getHeight());
            AppProperties.write(properties);
        } catch (IOException e) {
            AppProperties appProperties = new AppProperties();
            appProperties.setSize(stage.getWidth(), stage.getHeight());

            try {
                AppProperties.write(appProperties);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static AppProperties loadProperties() {
        try {
            return AppProperties.load();
        } catch (IOException | NullPointerException | NumberFormatException e) {
            AppProperties appProperties = new AppProperties();

            try {
                AppProperties.write(appProperties);
                return AppProperties.load();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineBean.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("RoutineBean");
        stage.setScene(scene);
        stage.setMinWidth(916);
        stage.setMinHeight(639);
        stage.getIcons().add(AppUtils.ICON);

        App.stage = stage;
        AppProperties properties = loadProperties();
        properties.setStageSize(stage);

        stage.show();
        stage.setOnCloseRequest(e -> writeProperties(properties, stage));

        if (properties.isCheckForUpdate()) {
            AppUtils.checkForUpdate();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}