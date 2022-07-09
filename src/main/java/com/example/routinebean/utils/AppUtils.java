package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.utils.properties.RoutineProperties;
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

    public static boolean createNewRoutine(String title, Routine routine) {
        String filteredFolderName = filterFolderName(title);

        boolean isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).exists();

        if (!isRoutinesDirCreated) {
            isRoutinesDirCreated = new File(AppData.ROUTINE_DIRECTORY).mkdirs();
        }

        boolean isRoutineCreated = false;

        if (isRoutinesDirCreated) {
            isRoutineCreated = new File(AppData.ROUTINE_DIRECTORY.concat(filteredFolderName)).mkdirs();
        }

        if (isRoutineCreated) {
            try {
                AppData.serialize(filteredFolderName, routine);
            } catch (IOException e) {
                isRoutineCreated = false;
            }
        }

        return isRoutineCreated;
    }

    public static void openExplorer(String directory) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select," + AppData.ROUTINE_DIRECTORY.concat(directory) + "\\routine.dat");
    }

    public static boolean createNewRoutineWithDialog() {
        boolean isRoutineCreated = false;

        TextInputDialog textDialog = new TextInputDialog("Routine");
        textDialog.setTitle("New Routine");
        textDialog.setHeaderText("Routine Name:");
        textDialog.getDialogPane().setPrefWidth(300);
        ((Stage) textDialog.getDialogPane().getScene().getWindow()).getIcons().add(ICON);

        Optional<String> result = textDialog.showAndWait();

        if (result.isPresent()) {
            isRoutineCreated = createNewRoutine(result.get(), new Routine(result.get()));
        }

        return isRoutineCreated;
    }

    public static boolean duplicateRoutine(String directory, Routine routine) {
        routine.setTitle(routine.getTitle().concat(" - Copy"));

        return createNewRoutine(directory.concat(" - Copy"), routine);
    }

    public static boolean deleteRoutine(String directory) {
        boolean isRoutineDeleted = false;

        File file = new File(AppData.ROUTINE_DIRECTORY.concat(directory));
        File[] fileContents = file.listFiles(File::isFile);

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
