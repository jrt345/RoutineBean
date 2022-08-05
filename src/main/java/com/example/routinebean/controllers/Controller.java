package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.data.Routine;
import com.example.routinebean.properties.RoutineProperties;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.UpdateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        textDialog.getDialogPane().setId("alertPane");
        textDialog.getDialogPane().getStylesheets().add(AppUtils.STYLESHEET);
        ((Stage) textDialog.getDialogPane().getScene().getWindow()).getIcons().add(AppUtils.ICON);

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            String directory = AppUtils.filterFolderName(result.get());
            Routine routine = new Routine(result.get());

            boolean isRoutineCreated = AppUtils.createNewRoutine(directory, routine);

            if (isRoutineCreated) {
                RoutineLoader loader = generateRoutineLoader(directory, routine);

                loaders.add(0, loader);
                routineVBox.getChildren().add(0, loader.getButton());
            }
        }
    }

    private RoutineLoader generateRoutineLoader(String directory, Routine routine) {
        Button button = generateButton(routine.getTitle());

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

        return loader;
    }

    private Button generateButton(String title) {
        Button button = new Button(title);
        button.setPrefSize(Double.MAX_VALUE, 40);
        button.setMinHeight(40);
        button.setId("routineButton");
        button.setFocusTraversable(false);
        button.getStylesheets().add(AppUtils.STYLESHEET);

        return button;
    }

    private ArrayList<RoutineLoader> generateRoutineLoaders(File[] files, ArrayList<Routine> routines) {
        ArrayList<RoutineLoader> routineLoaders = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (routines.get(i) != null) {
                String directory = files[i].getName();
                Routine routine = routines.get(i);

                routineLoaders.add(generateRoutineLoader(directory, routine));
            }
        }

        return routineLoaders;
    }

    public void runRoutine(RoutineLoader loader) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        RoutineController controller = fxmlLoader.getController();
        controller.setLoader(loader);
        controller.loadRoutine(loader.getRoutine());
        controller.loadTaskPresetData();

        Scene scene = new Scene(root, 1220, 820);
        Stage stage = new Stage();
        stage.setTitle(controller.getCurrentRoutineObject().getTitle());
        stage.setMinWidth(1236);
        stage.setMinHeight(859);
        stage.setScene(scene);
        stage.getIcons().add(AppUtils.ICON);

        RoutineProperties properties = RoutineProperties.load(loader.getDirectory()).orElse(new RoutineProperties(loader.getDirectory()));
        controller.setProperties(properties);
        properties.setStageSize(stage);

        controller.setStage(stage);
        controller.initializeMemento();

        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
                controller.closeRoutineOnCloseRequest(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void openGithub(ActionEvent event) {
        AppUtils.openUrlInBrowser("https://github.com/jrt345/RoutineBean");
    }

    @FXML
    public void openAboutBox(ActionEvent event) throws IOException {
        AppUtils.openAboutBox();
    }

    @FXML
    private void updateApp(ActionEvent event) throws IOException {
        UpdateManager.showUpdateDialog();
    }

    private ArrayList<Routine> getRoutines(File[] files) {
        ArrayList<Routine> routines = new ArrayList<>();

        for (File file : files) {
            routines.add(Routine.deserialize(file.getName()).orElse(null));
        }

        return routines;
    }

    private ContextMenu generateContextMenu(RoutineLoader loader) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem open = new MenuItem("Open");
        MenuItem rename = new MenuItem("Rename");
        MenuItem explorer = new MenuItem("Open in Explorer");
        MenuItem duplicate = new MenuItem("Duplicate");
        MenuItem delete = new MenuItem("Delete");

        open.setOnAction(event -> loader.getButton().fire());
        rename.setOnAction(event -> renameRoutine(loader));
        explorer.setOnAction(event -> openRoutineInExplorer(loader));
        duplicate.setOnAction(event -> duplicateRoutine(loader));
        delete.setOnAction(event -> deleteRoutine(loader));

        contextMenu.getItems().add(open);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(rename);
        contextMenu.getItems().add(explorer);
        contextMenu.getItems().add(duplicate);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(delete);

        return contextMenu;
    }

    private void renameRoutine(RoutineLoader loader) {
        TextInputDialog textDialog = new TextInputDialog(loader.getRoutine().getTitle());
        textDialog.setTitle("Rename Routine");
        textDialog.setHeaderText(null);
        textDialog.getDialogPane().setPrefWidth(300);
        textDialog.getDialogPane().setId("alertPane");
        textDialog.getDialogPane().getStylesheets().add(AppUtils.STYLESHEET);
        ((Stage) textDialog.getDialogPane().getScene().getWindow()).getIcons().add(AppUtils.ICON);

        Optional<String> result = textDialog.showAndWait();

        result.ifPresent(string -> loader.getRoutine().setTitle(string));
        result.ifPresent(string -> loader.getButton().setText(string));

        if (result.isPresent()) {
            try {
                Routine.serialize(loader.getDirectory(), loader.getRoutine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void openRoutineInExplorer(RoutineLoader loader) {
        AppUtils.openDirectory(AppUtils.createRoutineFile(loader.getDirectory()));
    }

    private void duplicateRoutine(RoutineLoader loader) {
        String directory = AppUtils.filterFolderName(loader.getDirectory().concat(" - Copy"));

        Routine routine = new Routine(loader.getRoutine().getTitle().concat(" - Copy"),
                loader.getRoutine().getTasks(), loader.getRoutine().getBackgroundColors());

        boolean isRoutineDuplicated = AppUtils.createNewRoutine(directory, routine);

        if (isRoutineDuplicated) {
            RoutineLoader duplicatedLoader = generateRoutineLoader(directory, routine);

            loaders.add(0, duplicatedLoader);
            routineVBox.getChildren().add(0, duplicatedLoader.getButton());
        }
    }

    private void deleteRoutine(RoutineLoader loader) {
        Optional<ButtonType> result = showDeleteRoutineAlert(loader.getRoutine().getTitle());

        if (result.isPresent()) {
            if (result.get().equals(ButtonType.OK)) {
                File file = AppUtils.createRoutineFile(loader.getDirectory());
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
        }
    }

    private Optional<ButtonType> showDeleteRoutineAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Routine");
        alert.setHeaderText(null);

        alert.getDialogPane().setId("alertPane");
        alert.getDialogPane().getStylesheets().add(AppUtils.STYLESHEET);
        alert.getDialogPane().setContentText("Are you sure you want to delete " + title + "?");

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(AppUtils.ICON);

        return alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boolean hasRoutinesDirectory = AppUtils.ROUTINES_DIRECTORY.exists();

        if (!hasRoutinesDirectory){
            hasRoutinesDirectory = AppUtils.ROUTINES_DIRECTORY.mkdirs();
        }

        if (hasRoutinesDirectory) {
            File[] files = AppUtils.ROUTINES_DIRECTORY.listFiles(File::isDirectory);

            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                loaders = generateRoutineLoaders(files, getRoutines(files));

                for (RoutineLoader loader : loaders) {
                    routineVBox.getChildren().add(loader.getButton());
                    routineVBox.setFillWidth(true);
                }
            }
        }

        if (!UpdateManager.isUpdateAvailable()) {
            updateButton.setDisable(true);
            updateButton.setOpacity(0.0);
        }
    }
}
