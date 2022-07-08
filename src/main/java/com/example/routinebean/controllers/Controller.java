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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

        for (File file : files) {
            try {
                routines.add(AppData.deserialize(file.getName()));
            } catch (IOException | ClassNotFoundException e) {
                routines.add(null);
            }
        }

        return routines;
    }

    private ArrayList<Button> getRoutineButtons(File[] files, ArrayList<Routine> routines) {
        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (routines.get(i) != null){
                String directory = files[i].getName();
                String title = routines.get(i).getTitle();
                buttons.add(generateButton(title, directory, routines.get(i)));
            }
        }

        return buttons;
    }

    private Button generateButton(String title, String directory, Routine routine) {
        Button button = new Button(title);
        button.setPrefSize(Double.MAX_VALUE, 40);
        button.setMinHeight(40);
        button.setOnAction(event -> openRoutine(directory));

        button.setContextMenu(generateContextMenu(button, directory, routine));

        return button;
    }

    private void openRoutine(String directory) {
        try {
            AppUtils.openRoutine(directory);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ContextMenu generateContextMenu(Button button, String directory, Routine routine) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem open = new MenuItem("Open");
        MenuItem explorer = new MenuItem("Open in Explorer");
        MenuItem duplicate = new MenuItem("Duplicate");
        MenuItem delete = new MenuItem("Delete");

        open.setOnAction(event -> button.fire());
        explorer.setOnAction(event -> openRoutineInExplorer(directory));
        duplicate.setOnAction(event -> duplicateRoutine(directory, routine));
        delete.setOnAction(event -> deleteRoutine(directory));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(duplicate);
        contextMenu.getItems().add(delete);

        return contextMenu;
    }

    private void refreshVBox() {
        File[] files = new File(AppData.ROUTINE_DIRECTORY).listFiles(File::isDirectory);

        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

            routineVBox.getChildren().clear();
            loadRoutineVBoxButtons(getRoutineButtons(files, getRoutines(files)));
        }
    }

    private void openRoutineInExplorer(String directory) {
        try {
            AppUtils.openExplorer(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void duplicateRoutine(String directory, Routine routine) {
        try {
            routine.setTitle(routine.getTitle().concat(" - Copy"));
            AppUtils.createNewRoutine(directory, routine);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        refreshVBox();
    }

    private void deleteRoutine(String directory) {
        boolean isRoutineDeleted = AppUtils.deleteRoutine(directory);

        if (isRoutineDeleted) {
            refreshVBox();
        }
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

        boolean hasRoutineDirectory = file.exists();
        if (!hasRoutineDirectory){
            hasRoutineDirectory = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        if (hasRoutineDirectory) {
            File[] files = file.listFiles(File::isDirectory);

            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                ArrayList<Routine> routines = getRoutines(files);

                loadRoutineVBoxButtons(getRoutineButtons(files, routines));
            }

        }

        updateButton.setDisable(true);
        updateButton.setOpacity(0.0);
    }
}
