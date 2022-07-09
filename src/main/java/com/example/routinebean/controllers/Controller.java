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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    private ArrayList<RoutineLoader> loaders = new ArrayList<>();

    @FXML
    private VBox routineVBox;

    @FXML
    private Button updateButton;

    @FXML
    private void newRoutine(ActionEvent event) {
        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");
        textDialog.getDialogPane().setPrefWidth(300);
        ((Stage) textDialog.getDialogPane().getScene().getWindow()).getIcons().add(AppUtils.ICON);

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            String title = result.get();
            String directory = AppUtils.filterFolderName(title);
            Routine routine = new Routine(title);

            boolean isRoutineCreated = AppUtils.createNewRoutine(directory, routine);

            if (isRoutineCreated) {
                Button button = generateButton(title, directory, routine);

                loaders.add(0, new RoutineLoader(directory, routine, button));
                routineVBox.getChildren().add(0, button);
                button.fire();
            }
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
        duplicate.setOnAction(event -> duplicateRoutine(directory));
        delete.setOnAction(event -> deleteRoutine(directory));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(duplicate);
        contextMenu.getItems().add(delete);

        return contextMenu;
    }

    private void openRoutineInExplorer(String directory) {
        try {
            AppUtils.openExplorer(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void duplicateRoutine(String directory) {
        Routine routine;
        try {
            routine = AppData.deserialize(directory);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        routine.setTitle(routine.getTitle().concat(" - Copy"));
        String title = routine.getTitle();

        directory = AppUtils.filterFolderName(directory.concat(" - Copy"));



        boolean isRoutineDuplicated = AppUtils.createNewRoutine(directory, routine);

        if (isRoutineDuplicated) {
            Button button = generateButton(title, directory, routine);

            loaders.add(0, new RoutineLoader(directory, routine, button));
            routineVBox.getChildren().add(0, button);
            button.fire();
        }
    }

    private void deleteRoutine(String directory) {
        File file = new File(AppData.ROUTINE_DIRECTORY.concat(directory));
        File[] fileContents = file.listFiles(File::isFile);

        if (fileContents != null) {
            Boolean[] areFileContentsDeleted = new Boolean[fileContents.length];

            for (int i = 0;i < fileContents.length;i++) {
                areFileContentsDeleted[i] = fileContents[i].delete();
            }

            if (!(Arrays.asList(areFileContentsDeleted).contains(false))){
                boolean isRoutineDeleted = file.delete();

                if (isRoutineDeleted) {
                    for (int i = 0; i < loaders.size(); i++) {
                        if (loaders.get(i).getDirectory().equals(directory)) {
                            routineVBox.getChildren().remove(i);
                            loaders.remove(i);
                            break;
                        }
                    }
                }
            }
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
                loaders = generateRoutineLoaders(files, getRoutines(files));

                for (RoutineLoader loader : loaders) {
                    routineVBox.getChildren().add(loader.getButton());
                    routineVBox.setFillWidth(true);
                }
            }
        }

        updateButton.setDisable(true);
        updateButton.setOpacity(0.0);
    }
}
