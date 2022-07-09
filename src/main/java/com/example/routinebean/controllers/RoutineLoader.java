package com.example.routinebean.controllers;

import com.example.routinebean.App;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.Routine;
import com.example.routinebean.utils.properties.RoutineProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

class RoutineLoader {

    private String directory;
    private Routine routine;
    private Button button;

    RoutineLoader(String directory, Routine routine, Button button) {
        this.directory = directory;
        this.routine = routine;
        this.button = button;
        button.setOnAction(e -> {
            try {
                run();
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void run() throws IOException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("routineLoader.fxml"));
        Parent root = fxmlLoader.load();

        routine = AppData.deserialize(directory);
        RoutineController controller = fxmlLoader.getController();
        controller.loadRoutine(routine);
        controller.setFolderName(directory);
        controller.setButton(button);
        controller.getButton().setDisable(true);

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
        loadProperties(directory);

        stage.show();

        stage.setOnCloseRequest(e -> {
            writeProperties(directory, stage);
            controller.getButton().setDisable(false);
        });
    }

    private void writeProperties(String directory, Stage stage) {
        RoutineProperties.setWidth(stage.getWidth());
        RoutineProperties.setHeight(stage.getHeight());

        try {
            RoutineProperties.write(directory);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadProperties(String directory) throws IOException {
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

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutineLoader loader = (RoutineLoader) o;

        if (!directory.equals(loader.directory)) return false;
        if (!routine.equals(loader.routine)) return false;
        return button.equals(loader.button);
    }

    @Override
    public int hashCode() {
        int result = directory.hashCode();
        result = 31 * result + routine.hashCode();
        result = 31 * result + button.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RoutineLoader{" +
                "directory='" + directory + '\'' +
                ", routine=" + routine +
                ", button=" + button +
                '}';
    }
}
