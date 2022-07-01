package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button updateButton;

    @FXML
    private void newRoutine(ActionEvent event) throws IOException {
        AppUtils.newRoutine();
    }

    @FXML
    private void openRoutine(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("routineExplorer.fxml")));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void openGithub(ActionEvent event) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String url = "https://github.com/jrt345/RoutineBean";
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    }

    @FXML
    private void updateApp(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateButton.setDisable(true);
        updateButton.setOpacity(0);
    }
}