package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.Routine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class Controller {

    //Name of the routine folder inside the "routines" directory
    private String routineFolderName;

    //Creates a new routine folder, based on the user's text input
    private void createNewRoutineFolder(String title, Routine routine) throws IOException {
        //The name of the routine folder
        routineFolderName = title;

        //Is the "routines" directory created?
        boolean isRoutinesDirCreated = new File(AppData.userDataDir).exists();

        //If "routines" directory is not created, the directory will be created
        if (!isRoutinesDirCreated) {
            isRoutinesDirCreated = new File(AppData.userDataDir).mkdirs();
        }

        //The new routine folder has not been created yet
        boolean routineFolder = false;

        if (isRoutinesDirCreated) {
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
                String modifiedFolderName = title + " (" + index + ")";

                file = new File(AppData.userDataDir.concat(modifiedFolderName));
                if (!file.exists()){
                    routineFolderName = modifiedFolderName;
                    routineFolder = file.mkdirs();
                }
            }
        }

        //If the new routine folder exists, a "routine.dat" will be created
        if (routineFolder) {
            AppData.serialize(routineFolderName, routine);
        }
    }

    //Runs the routineLoader.fxml file when both adding or opening a routine folder
    private void runRoutineLoader(String title, Routine routine) throws IOException {
        /*If title is null and routine is not, then it is loading an existing routine
        * If routine is null and title is not, then it is creating a new routine*/

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        RoutineController controller = fxmlLoader.getController();
        controller.setRoutine(Objects.requireNonNullElseGet(routine, () -> new Routine(title)));
        controller.loadRoutine();

        if (title != null) {
            createNewRoutineFolder(title, controller.getRoutine());
        }

        controller.setFolderName(routineFolderName);

        Stage stage = new Stage();
        stage.setTitle(controller.getRoutine().getTitle());
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void newRoutine(ActionEvent event) throws IOException {
        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            runRoutineLoader(result.get(), null);
        }
    }

    @FXML
    private void openRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(AppData.userDataDir));
        directoryChooser.setTitle("Open Routine");

        File selectedDirectory = directoryChooser.showDialog(App.getStage());

        if (selectedDirectory != null){
            routineFolderName = selectedDirectory.getName();

            Routine routine = AppData.deserialize(selectedDirectory.getName());

            runRoutineLoader(null, routine);
        }
    }

    @FXML
    private void openGithub(ActionEvent event) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String url = "https://github.com/jrt345/RoutineBean";
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    }
}