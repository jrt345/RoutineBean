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
    private void newRoutine(ActionEvent event) {
        boolean isRoutineCreated = AppUtils.createNewRoutineWithDialog();

        if (isRoutineCreated) {
            refreshVBox();

            ((Button) routineVBox.getChildren().get(routineVBox.getChildren().size() - 1)).fire();
        }
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

    private ArrayList<RoutineLoader> generateRoutineLoaders(File[] files, ArrayList<Routine> routines) {
        ArrayList<RoutineLoader> routineLoaders = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (routines.get(i) != null) {
                String title = routines.get(i).getTitle();
                String directory = files[i].getName();
                Routine routine = routines.get(i);
                Button button = generateButton(title, directory, routine);
                RoutineLoader loader = new RoutineLoader(directory, routine, button);
                routineLoaders.add(loader);
            }
        }

        return routineLoaders;
    }

    private Button generateButton(String title, String directory, Routine routine) {
        Button button = new Button(title);
        button.setPrefSize(Double.MAX_VALUE, 40);
        button.setMinHeight(40);

        button.setContextMenu(generateContextMenu(button, directory, routine));

        return button;
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
        File routinesDirectory = new File(AppData.ROUTINE_DIRECTORY);
        File[] files = routinesDirectory.listFiles(File::isDirectory);

        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            ArrayList<RoutineLoader> loaders = generateRoutineLoaders(files, getRoutines(files));

            routineVBox.getChildren().clear();
            loadRoutineVBoxButtons(loaders);
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
        boolean isRoutineDuplicated = AppUtils.duplicateRoutine(directory, routine);

        if (isRoutineDuplicated) {
            refreshVBox();
        }
    }

    private void deleteRoutine(String directory) {
        boolean isRoutineDeleted = AppUtils.deleteRoutine(directory);

        if (isRoutineDeleted) {
            refreshVBox();
        }
    }

    private void loadRoutineVBoxButtons(ArrayList<RoutineLoader> loaders) {
        for (RoutineLoader loader : loaders) {
            routineVBox.getChildren().add(loader.getButton());
            routineVBox.setFillWidth(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File routinesDirectory = new File(AppData.ROUTINE_DIRECTORY);

        boolean hasRoutinesDirectory = routinesDirectory.exists();
        if (!hasRoutinesDirectory){
            hasRoutinesDirectory = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        if (hasRoutinesDirectory) {
            File[] files = routinesDirectory.listFiles(File::isDirectory);

            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                loadRoutineVBoxButtons(generateRoutineLoaders(files, getRoutines(files)));
            }
        }

        updateButton.setDisable(true);
        updateButton.setOpacity(0.0);
    }
}
