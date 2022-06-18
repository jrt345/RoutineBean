package com.example.routinebean;

import com.example.routinebean.utils.AppProperties;
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

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineBean.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("RoutineBean");
        stage.setScene(scene);
        stage.setMinHeight(639);
        stage.setMinWidth(916);
        AppProperties.setWindowSizeFromProperties(stage, true);
        stage.show();
        App.stage = stage;
        stage.setOnCloseRequest(e -> AppProperties.saveProperties(stage, true));
    }

    public static void main(String[] args) {
        launch();
    }
}