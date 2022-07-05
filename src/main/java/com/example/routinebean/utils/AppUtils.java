package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.controllers.RoutineController;
import com.example.routinebean.utils.properties.RoutineProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class AppUtils {

    public static final Image ICON = new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/routinebean-logo.png")));

    public static void writeProperties(String directory, Stage stage) {
        RoutineProperties.setWidth(stage.getWidth());
        RoutineProperties.setHeight(stage.getHeight());

        try {
            RoutineProperties.write(directory);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void loadProperties(String directory) throws IOException {
        if (new File(AppData.ROUTINE_DIRECTORY.concat(directory + "\\Routine.properties")).exists()) {
            try {
                RoutineProperties.load(directory);
            } catch (IOException | NullPointerException | NumberFormatException e) {
                RoutineProperties.loadDefaultProperties(directory);
            }
        } else {
            RoutineProperties.loadDefaultProperties(directory);
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

    public static void createNewRoutine(String title, Routine routine) throws IOException, ClassNotFoundException {
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
            runRoutineLoader(filteredFolderName);
        }
    }

    public static void openExplorer(String directory) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select," + AppData.ROUTINE_DIRECTORY.concat(directory) + "\\routine.dat");
    }

    private static void runRoutineLoader(String directory) throws IOException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        RoutineController controller = fxmlLoader.getController();
        controller.loadRoutine(AppData.deserialize(directory));
        controller.setFolderName(directory);

        Scene scene = new Scene(root, 900, 600);
        Stage stage = new Stage();
        stage.setTitle(controller.getCurrentRoutineObject().getTitle());
        stage.setMinHeight(639);
        stage.setMinWidth(916);
        stage.setScene(scene);
        stage.getIcons().add(ICON);

        controller.setStage(stage);
        controller.initializeSaveState();

        RoutineProperties.setStage(stage);
        loadProperties(directory);

        stage.show();

        stage.setOnCloseRequest(e -> writeProperties(directory, stage));
    }

    public static void newRoutine() throws IOException, ClassNotFoundException {
        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");
        textDialog.getDialogPane().setPrefWidth(300);
        ((Stage) textDialog.getDialogPane().getScene().getWindow()).getIcons().add(ICON);

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            createNewRoutine(result.get(), new Routine(result.get()));
        }
    }

    public static void openRoutine(String directory) throws IOException, ClassNotFoundException {
        runRoutineLoader(directory);
    }

    public static boolean deleteRoutine(String directory) {
        File file = new File(AppData.ROUTINE_DIRECTORY.concat(directory));
        File[] fileContents = file.listFiles(File::isFile);
        boolean isRoutineDeleted = false;

        if (fileContents != null) {
            Boolean[] areFileContentsDeleted = new Boolean[fileContents.length];

            for (int i = 0;i < fileContents.length;i++) {
                areFileContentsDeleted[i] = fileContents[i].delete();
            }

            if (!(Arrays.asList(areFileContentsDeleted).contains(false))){
                isRoutineDeleted = file.delete();
            }
        }

        return isRoutineDeleted;
    }
}
