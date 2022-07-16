package com.example.routinebean.controllers;

import com.example.routinebean.utils.AppUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class AboutBoxController {

    @FXML
    private Button okButton;

    @FXML
    private void closeAboutBox(ActionEvent event) {
        ((Stage) okButton.getScene().getWindow()).close();
    }

    @FXML
    private void openLink(ActionEvent event) throws IOException, URISyntaxException {
        AppUtils.openUrlInBrowser("https://www.gnu.org/licenses/gpl-3.0.en.html");
    }
}
