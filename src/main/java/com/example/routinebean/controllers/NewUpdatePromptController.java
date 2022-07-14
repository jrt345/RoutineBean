package com.example.routinebean.controllers;

import com.example.routinebean.utils.UpdateManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class NewUpdatePromptController implements Initializable {
    @FXML
    private Label currentVersionLabel;

    @FXML
    private Button downloadButton;

    @FXML
    private Label latestVersionLabel;

    @FXML
    private Label newUpdateInfoLabel;

    @FXML
    private void download(ActionEvent event) {

    }

    @FXML
    private void ignore(ActionEvent event) {

    }

    @FXML
    private void learnMore(ActionEvent event) {

    }

    @FXML
    private void remindLater(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(downloadButton::requestFocus);

        String latestVersion = UpdateManager.getLatestVersion();

        currentVersionLabel.setText(UpdateManager.CURRENT_VERSION);
        latestVersionLabel.setText(latestVersion);
        newUpdateInfoLabel.setText(("RoutineBean " + latestVersion +
                " has been released. " + "Download and install it now, or select 'Learn more'" +
                " to visit https://github.com/jrt345/RoutineBean/releases/latest/ " +
                "for more information."));
    }
}
