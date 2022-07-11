package com.example.routinebean.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutBoxController {

    @FXML
    private Button okButton;

    @FXML
    private void closeAboutBox(ActionEvent event) {
        ((Stage) okButton.getScene().getWindow()).close();
    }

    @FXML
    private void openLink(ActionEvent event) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String url = "https://www.gnu.org/licenses/gpl-3.0.en.html";
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    }
}
