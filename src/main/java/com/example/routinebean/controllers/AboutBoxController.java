package com.example.routinebean.controllers;

import com.example.routinebean.utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutBoxController {

    @FXML
    private Button okButton;

    @FXML
    private void closeAboutBox() {
        ((Stage) okButton.getScene().getWindow()).close();
    }

    @FXML
    private void openLink() {
        AppUtils.openUrlInBrowser("https://www.gnu.org/licenses/gpl-3.0.en.html");
    }
}
