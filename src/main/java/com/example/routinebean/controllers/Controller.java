package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.Routine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {

    @FXML
    private void newRoutine(ActionEvent event) throws IOException {
        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
            Parent root = fxmlLoader.load();

            boolean isUserDataCreated = new File(AppData.userDataDir).exists();

            if (!isUserDataCreated) {
                isUserDataCreated = new File(AppData.userDataDir).mkdirs();
            }

            boolean routineFolder = false;
            if (isUserDataCreated) {
                routineFolder = new File(AppData.userDataDir.concat(result.get())).mkdirs();
            }

            RoutineController controller = fxmlLoader.getController();
            controller.setRoutine(new Routine(result.get()));
            controller.loadRoutine();

            Stage stage = new Stage();
            stage.setTitle("title");
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void openRoutine(ActionEvent event) {

    }

    @FXML
    private void openSettings(ActionEvent event) {

    }
}