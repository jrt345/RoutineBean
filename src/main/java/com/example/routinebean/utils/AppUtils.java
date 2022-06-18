package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.controllers.RoutineController;
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

    //Name of the routine folder inside the "routines" directory
    private static String routineFolderName;

    private static void createNewRoutineFolder(String title, Routine routine) throws IOException {
        routineFolderName = title;

        //Is the "routines" directory created?
        boolean isRoutinesDirCreated = new File(AppData.userDataDir).exists();

        if (!isRoutinesDirCreated) {
            isRoutinesDirCreated = new File(AppData.userDataDir).mkdirs();
        }

        //The new routine folder has not been created yet
        boolean routineFolder = false;

        if (isRoutinesDirCreated) {
            //Replaces any illegal characters in routineFolderName with underscores
            routineFolderName = routineFolderName.replaceAll("[<>:\"/\\\\|?.*]", "_");

            //Removes any spaces before and after routineFolderName
            routineFolderName = routineFolderName.trim();

            //Adds underscores before and after any illegal folder names in Windows
            if (Pattern.matches("CON$|PRN$|AUX$|NUL$|COM1$|COM2$|COM3$|COM4$|COM5$|COM6$|COM7$|COM8$|COM9$|LPT1$|LPT2$|LPT3$|LPT4$|LPT5$|LPT6$|LPT7$|LPT8$|LPT9$", routineFolderName)) {
                routineFolderName = "_" + routineFolderName + "_";
            }

            //Current new routine folder file object
            File file = new File(AppData.userDataDir.concat(routineFolderName));

            //If the new routine folder does not exist, the folder will be created
            if (!file.exists()){
                routineFolder = new File(AppData.userDataDir.concat(routineFolderName)).mkdirs();
            }

            int index = 0;

            /*If the new routine folder already exists, add "(index)" to the end of the
             * folder name until such folder does not exist and then the folder,
             * with the modified folder name, will be created*/
            while (file.exists() && !routineFolder){
                index++;
                String modifiedFolderName = routineFolderName + " (" + index + ")";

                file = new File(AppData.userDataDir.concat(modifiedFolderName));
                if (!file.exists()){
                    routineFolderName = modifiedFolderName;
                    routineFolder = file.mkdirs();
                }
            }
        }

        if (routineFolder) {
            AppData.serialize(routineFolderName, routine);
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

        Stage stage = new Stage();
        stage.setTitle(controller.getCurrentRoutineObject().getTitle());
        stage.setMinHeight(600);
        stage.setMinWidth(900);
        controller.setStage(stage);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        AppProperties.setWindowSizeFromProperties(stage, false);
        stage.show();

        stage.setOnCloseRequest(e -> AppProperties.saveProperties(stage, false));
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
        directoryChooser.setInitialDirectory(new File(AppData.userDataDir));
        directoryChooser.setTitle("Open Routine");

        File selectedDirectory = directoryChooser.showDialog(currentStage);

        if (selectedDirectory != null){
            routineFolderName = selectedDirectory.getName();

            Routine routine = AppData.deserialize(selectedDirectory.getName());

            runRoutineLoader(null, routine);
        }
    }
}
