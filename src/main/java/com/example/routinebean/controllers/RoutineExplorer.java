package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.Routine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class RoutineExplorer implements Initializable {
    @FXML
    private VBox routineVBox;

    @FXML
    private Button updateButton;

    @FXML
    private ScrollPane vBoxScrollPane;

    @FXML
    private void returnToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("routineBean.fxml")));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File(AppData.ROUTINE_DIRECTORY);

        File[] files = file.listFiles(File::isDirectory);

        ArrayList<Routine> routines = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            try {
                routines.add(AppData.deserialize(files[i].getName()));
            } catch (IOException | ClassNotFoundException e) {
                routines.add(null);
            }
        }

        updateButton.setOpacity(0.0);
    }
}
