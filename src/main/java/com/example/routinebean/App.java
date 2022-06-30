package com.example.routinebean;

import com.example.routinebean.utils.properties.AppProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    private void writeProperties() {
        AppProperties.setWidth(stage.getWidth());
        AppProperties.setHeight(stage.getHeight());

        try {
            AppProperties.write();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadProperties() throws IOException {
        if (new File(System.getProperty("user.dir").concat("\\RoutineBean.properties")).exists()) {
            try {
                AppProperties.load();
            } catch (IOException | NullPointerException | NumberFormatException e) {
                AppProperties.loadDefaultProperties();
            }
        } else {
            AppProperties.loadDefaultProperties();
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

        App.stage = stage;
        AppProperties.setStage(stage);
        loadProperties();

        stage.show();
        stage.setOnCloseRequest(e -> writeProperties());
    }

    public static void main(String[] args) {
        launch();
    }
}