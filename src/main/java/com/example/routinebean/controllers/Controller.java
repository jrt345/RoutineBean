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
        refreshVBox();
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

    private void deleteRoutine(String directory) {
        boolean isRoutineDeleted = AppUtils.deleteRoutine(directory);

        if (isRoutineDeleted) {
            refreshVBox();
        }
    }

    private void refreshVBox() {
        files = new File(AppData.ROUTINE_DIRECTORY).listFiles(File::isDirectory);

        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            routines = getRoutines(files);
            routineVBox.getChildren().clear();

            loadRoutineVBoxButtons(getRoutineButtons(files, getRoutines(files)));
        }
    }

    private ContextMenu generateContextMenu(Button button, String directory, int index) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem open = new MenuItem("Open");
        MenuItem explorer = new MenuItem("Open in Explorer");
        MenuItem duplicate = new MenuItem("Duplicate");
        MenuItem delete = new MenuItem("Delete");

        open.setOnAction(event -> button.fire());
        explorer.setOnAction(event -> {
            try {
                AppUtils.openExplorer(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        duplicate.setOnAction(event -> {
            try {
                AppUtils.createNewRoutine(files[index].getName(), routines.get(index));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            refreshVBox();

        });
        delete.setOnAction(event -> deleteRoutine(directory));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(duplicate);
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

    private ArrayList<Button> getRoutineButtons(File[] files, ArrayList<Routine> routines) {
        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (routines.get(i) != null){
                String directory = files[i].getName();
                String title = routines.get(i).getTitle();
                buttons.add(generateButton(title, directory, i));
            }
        }

        return buttons;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File(AppData.ROUTINE_DIRECTORY);

        boolean hasRoutineDirectory = file.exists();
        if (!hasRoutineDirectory){
            hasRoutineDirectory = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        if (hasRoutineDirectory) {
            files = file.listFiles(File::isDirectory);

            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                routines = getRoutines(files);

                loadRoutineVBoxButtons(getRoutineButtons(files, routines));
            }

        }

        updateButton.setDisable(true);
        updateButton.setOpacity(0.0);
    }
}
