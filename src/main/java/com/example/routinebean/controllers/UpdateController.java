package com.example.routinebean.controllers;

import com.example.routinebean.properties.AppProperties;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.UpdateManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateController implements Initializable {

    private AppProperties properties;
    public void setProperties(AppProperties properties) {
        this.properties = properties;
    }

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Button downloadButton;

    @FXML
    private Label latestVersionLabel;

    @FXML
    private Label newUpdateInfoLabel;

    @FXML
    private void download() {
        ((Stage) downloadButton.getScene().getWindow()).close();

        AppUtils.openUrlInBrowser("https://github.com/jrt345/RoutineBean/releases/latest");
    }

    @FXML
    private void ignore() throws IOException {
        if (properties != null) {
            properties.setCheckForUpdate(false);
            AppProperties.write(properties);
        }

        ((Stage) downloadButton.getScene().getWindow()).close();
    }

    @FXML
    private void learnMore() {
        ((Stage) downloadButton.getScene().getWindow()).close();

        AppUtils.openUrlInBrowser("https://github.com/jrt345/RoutineBean/releases/latest");
    }

    @FXML
    private void remindLater() {
        ((Stage) downloadButton.getScene().getWindow()).close();
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
