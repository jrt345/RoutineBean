package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.controllers.RoutineController;
import com.example.routinebean.utils.properties.RoutineProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class AppUtils {

    private static String routineFolderName;

    private static void writeProperties(Stage stage) {
        RoutineProperties.setWidth(stage.getWidth());
        RoutineProperties.setHeight(stage.getHeight());

        try {
            RoutineProperties.write(routineFolderName);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void loadProperties() throws IOException {
        if (new File(AppData.ROUTINE_DIRECTORY.concat(routineFolderName + "\\Routine.properties")).exists()) {
            try {
                RoutineProperties.load(routineFolderName);
            } catch (IOException | NullPointerException | NumberFormatException e) {
                RoutineProperties.loadDefaultProperties(routineFolderName);
            }
        } else {
            RoutineProperties.loadDefaultProperties(routineFolderName);
        }
    }

    private static String getDuplicateFolderName(String name) {
        int index = 0;
        String duplicateFolderName = name;
        File file = new File(AppData.ROUTINE_DIRECTORY.concat(name));

        while (file.exists()){
            index++;
            duplicateFolderName = name + " (" + index + ")";

            file = new File(AppData.ROUTINE_DIRECTORY.concat(duplicateFolderName));
        }

        return duplicateFolderName;
    }

    private static String filterFolderName(String name) {
        String filteredFolderName = name;

        filteredFolderName = filteredFolderName.replaceAll("[<>:\"/\\\\|?.*]", "_");

        filteredFolderName = filteredFolderName.trim();

        if (filteredFolderName.equals("")){
            filteredFolderName = "Routine";
        }

        if (Pattern.matches("CON$|PRN$|AUX$|NUL$|COM1$|COM2$|COM3$|COM4$|COM5$|COM6$|COM7$|COM8$|COM9$|LPT1$|LPT2$|LPT3$|LPT4$|LPT5$|LPT6$|LPT7$|LPT8$|LPT9$", filteredFolderName)) {
            filteredFolderName = "_" + filteredFolderName + "_";
        }

        if (new File(AppData.ROUTINE_DIRECTORY.concat(filteredFolderName)).exists()){
            filteredFolderName = getDuplicateFolderName(filteredFolderName);
        }

        return filteredFolderName;
    }

    private static void createNewRoutineFolder(String title, Routine routine) throws IOException {
        String filteredFolderName = filterFolderName(title);

        boolean isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).exists();

        if (!isRoutinesDirCreated) {
            isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        boolean newRoutineFolder = false;

        if (isRoutinesDirCreated) {
            newRoutineFolder = new File(AppData.ROUTINE_DIRECTORY.concat(filteredFolderName)).mkdirs();
        }

        if (newRoutineFolder) {
            AppData.serialize(filteredFolderName, routine);
            routineFolderName = filteredFolderName;
        }
    }

    private static void runRoutineLoader(String title, Routine routine) throws IOException {
        /*If title is null and routine is not, then it is loading an existing routine
         * If routine is null and title is not, then it is creating a new routine*/

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        RoutineController controller = fxmlLoader.getController();
        controller.loadRoutine(Objects.requireNonNullElseGet(routine, () -> new Routine(title)));

        if (title != null) {
            createNewRoutineFolder(title, controller.getCurrentRoutineObject());
        }

        controller.setFolderName(routineFolderName);

        Scene scene = new Scene(root, 900, 600);
        Stage stage = new Stage();
        stage.setTitle(controller.getCurrentRoutineObject().getTitle());
        stage.setMinHeight(639);
        stage.setMinWidth(916);
        stage.setScene(scene);

        controller.setStage(stage);
        controller.initializeSaveState();

        RoutineProperties.setStage(stage);
        loadProperties();

        stage.show();

        stage.setOnCloseRequest(e -> writeProperties(stage));
    }

    public static void newRoutine() throws IOException {
        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            runRoutineLoader(result.get(), null);
        }
    }

    public static void openRoutine(Stage currentStage) throws IOException, ClassNotFoundException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(AppData.ROUTINE_DIRECTORY));
        directoryChooser.setTitle("Open Routine");

        File selectedDirectory = directoryChooser.showDialog(currentStage);

        if (selectedDirectory != null){
            routineFolderName = selectedDirectory.getName();

            Routine routine = AppData.deserialize(selectedDirectory.getName());

            runRoutineLoader(null, routine);
        }
    }
}
