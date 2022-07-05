package com.example.routinebean.controllers;

import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.Routine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    private File[] files;
    private ArrayList<Routine> routines;

    @FXML
    private VBox routineVBox;

    @FXML
    private Button updateButton;

    @FXML
    private void newRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        AppUtils.createNewRoutineWithDialog();
    }

    @FXML
    private void openSettings(ActionEvent event) {

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

    private ArrayList<Routine> getRoutines(File[] files) {
        ArrayList<Routine> routines = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            try {
                routines.add(AppData.deserialize(files[i].getName()));
            } catch (IOException | ClassNotFoundException e) {
                routines.add(null);
            }
        }

        return routines;
    }

    private void deleteRoutine(String directory, int index) {
        boolean isRoutineDeleted = AppUtils.deleteRoutine(directory);

        if (isRoutineDeleted) {
            routines = getRoutines(files);
            files = new File(AppData.ROUTINE_DIRECTORY).listFiles(File::isDirectory);
            routineVBox.getChildren().remove(index);
        }
    }

    private ContextMenu generateContextMenu(Button button, String directory, int index) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem open = new MenuItem("Open");
        MenuItem explorer = new MenuItem("Open in Explorer");
        MenuItem delete = new MenuItem("Delete");

        open.setOnAction(event -> button.fire());
        explorer.setOnAction(event -> {
            try {
                AppUtils.openExplorer(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        delete.setOnAction(event -> deleteRoutine(directory, index));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(delete);

        return contextMenu;
    }

    private Button generateButton(String title, String directory, int index) {
        Button button = new Button(title);
        button.setPrefSize(Double.MAX_VALUE, 40);
        button.setMinHeight(40);
        button.setOnAction(event -> {
            try {
                AppUtils.openRoutine(directory);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        button.setContextMenu(generateContextMenu(button, directory, index));

        return button;
    }

    private void loadRoutineVBoxButtons(ArrayList<Button> buttons) {
        for (Button button : buttons) {
            routineVBox.getChildren().add(button);
            routineVBox.setFillWidth(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File(AppData.ROUTINE_DIRECTORY);

        ArrayList<Button> buttons = new ArrayList<>();

        files = file.listFiles(File::isDirectory);
        routines = getRoutines(files);

        for (int i = 0; i < files.length; i++) {
            if (routines.get(i) != null){
                String directory = files[i].getName();
                String title = routines.get(i).getTitle();
                buttons.add(generateButton(title, directory, i));
            }
        }

        loadRoutineVBoxButtons(buttons);

        updateButton.setDisable(true);
        updateButton.setOpacity(0.0);
    }
}
