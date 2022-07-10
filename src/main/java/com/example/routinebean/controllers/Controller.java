package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.Routine;
import com.example.routinebean.utils.properties.RoutineProperties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public void runRoutine(RoutineLoader loader) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        RoutineController controller = fxmlLoader.getController();
        controller.setLoader(loader);
        controller.loadRoutine(loader.getRoutine());

        Scene scene = new Scene(root, 900, 600);
        Stage stage = new Stage();
        stage.setTitle(controller.getCurrentRoutineObject().getTitle());
        stage.setMinHeight(639);
        stage.setMinWidth(916);
        stage.setScene(scene);
        stage.getIcons().add(AppUtils.ICON);

        controller.setStage(stage);
        controller.initializeMemento();

        RoutineProperties.setStage(stage);

        stage.show();

        stage.setOnCloseRequest(e -> {
            controller.getLoader().getButton().setDisable(false);
        });
    }

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
                Button button = generateButton(title);

                RoutineLoader loader = new RoutineLoader(directory, routine, button);
                loader.getButton().setContextMenu(generateContextMenu(loader));
                loader.getButton().setOnAction(e -> {
                    try {
                        loader.getButton().setDisable(true);
                        runRoutine(loader);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                loaders.add(0, loader);
                routineVBox.getChildren().add(0, button);
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

                Button button = generateButton(title);

                RoutineLoader loader = new RoutineLoader(directory, routine, button);
                loader.getButton().setContextMenu(generateContextMenu(loader));
                loader.getButton().setOnAction(event -> {
                    try {
                        loader.getButton().setDisable(true);
                        runRoutine(loader);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                routineLoaders.add(loader);
            }
        }

        return routineLoaders;
    }

    private Button generateButton(String title) {
        Button button = new Button(title);
        button.setPrefSize(Double.MAX_VALUE, 40);
        button.setMinHeight(40);

        return button;
    }

    private ContextMenu generateContextMenu(RoutineLoader loader) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem open = new MenuItem("Open");
        MenuItem explorer = new MenuItem("Open in Explorer");
        MenuItem duplicate = new MenuItem("Duplicate");
        MenuItem delete = new MenuItem("Delete");

        open.setOnAction(event -> loader.getButton().fire());
        explorer.setOnAction(event -> openRoutineInExplorer(loader));
        duplicate.setOnAction(event -> duplicateRoutine(loader));
        delete.setOnAction(event -> deleteRoutine(loader));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(duplicate);
        contextMenu.getItems().add(delete);

        return contextMenu;
    }

    private void openRoutineInExplorer(RoutineLoader loader) {
        try {
            AppUtils.openExplorer(loader.getDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void duplicateRoutine(RoutineLoader loader) {
        String directory = loader.getDirectory();
        Routine routine = loader.getRoutine();

        directory = AppUtils.filterFolderName(directory.concat(" - Copy"));
        routine.setTitle(routine.getTitle().concat(" - Copy"));

        boolean isRoutineDuplicated = AppUtils.createNewRoutine(directory, routine);

        if (isRoutineDuplicated) {
            Button button = generateButton(routine.getTitle());

            RoutineLoader duplicatedLoader = new RoutineLoader(directory, routine, button);
            duplicatedLoader.getButton().setContextMenu(generateContextMenu(duplicatedLoader));
            duplicatedLoader.getButton().setOnAction(e -> {
                try {
                    duplicatedLoader.getButton().setDisable(true);
                    runRoutine(duplicatedLoader);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            loaders.add(0, duplicatedLoader);
            routineVBox.getChildren().add(0, button);
        }
    }

    private void deleteRoutine(RoutineLoader loader) {
        File file = new File(AppData.ROUTINE_DIRECTORY.concat(loader.getDirectory()));
        File[] fileContents = file.listFiles(File::isFile);

        if (fileContents != null) {
            Boolean[] areFileContentsDeleted = new Boolean[fileContents.length];

            for (int i = 0;i < fileContents.length;i++) {
                areFileContentsDeleted[i] = fileContents[i].delete();
            }

            if (!(Arrays.asList(areFileContentsDeleted).contains(false))){
                boolean isRoutineDeleted = file.delete();

                if (isRoutineDeleted) {
                    routineVBox.getChildren().remove(loader.getButton());
                    loaders.remove(loader);
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
