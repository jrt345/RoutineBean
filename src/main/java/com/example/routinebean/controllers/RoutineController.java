package com.example.routinebean.controllers;

import com.example.routinebean.commands.Caretaker;
import com.example.routinebean.commands.Originator;
import com.example.routinebean.data.AppData;
import com.example.routinebean.data.Routine;
import com.example.routinebean.properties.RoutineProperties;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.ColorUtils;
import com.example.routinebean.utils.UpdateManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoutineController implements Initializable {

    private static final ButtonType exitWithSaving = new ButtonType("Save");
    private static final ButtonType exitWithoutSaving = new ButtonType("Don't Save");
    private static final ButtonType cancel = new ButtonType("Cancel");

    private RoutineLoader loader;
    public void setLoader(RoutineLoader loader) {
        this.loader = loader;
    }

    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private RoutineProperties properties;
    public void setProperties(RoutineProperties properties) {
        this.properties = properties;
    }

    private final Originator originator = new Originator();
    private final Caretaker caretaker = new Caretaker();

    private int savedRoutinesIndex = 0;
    private int currentRoutineIndex = 0;

    private final TextField[][] routineTextFields = new TextField[24][7];
    private final String[][] routineBackgroundColors = new String[24][7];

    @FXML
    private Label title;

    @FXML
    private MenuItem saveButton;

    @FXML
    private MenuItem closeButton;

    @FXML
    private MenuItem undoButton;

    @FXML
    private MenuItem redoButton;

    @FXML
    private TextField titleTextField;

    @FXML
    private GridPane routineGrid;

    private final TextField[] taskTextFields = new TextField[7];

    private final ColorPicker[] taskColorPickers = new ColorPicker[7];

    @FXML
    private Button updateRoutineButton;

    @FXML
    private TextField mondayTextField;

    @FXML
    private ColorPicker mondayColorPicker;

    @FXML
    private TextField tuesdayTextField;

    @FXML
    private ColorPicker tuesdayColorPicker;

    @FXML
    private TextField wednesdayTextField;

    @FXML
    private ColorPicker wednesdayColorPicker;

    @FXML
    private TextField thursdayTextField;

    @FXML
    private ColorPicker thursdayColorPicker;

    @FXML
    private TextField fridayTextField;

    @FXML
    private ColorPicker fridayColorPicker;

    @FXML
    private TextField saturdayTextField;

    @FXML
    private ColorPicker saturdayColorPicker;

    @FXML
    private TextField sundayTextField;

    @FXML
    private ColorPicker sundayColorPicker;

    @FXML
    private void saveRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        Routine currentRoutine = getCurrentRoutineObject();
        Routine savedRoutine = AppData.deserialize(loader.getDirectory());

        if (!savedRoutine.equals(currentRoutine)){
            loader.setRoutine(currentRoutine);
            loader.getButton().setText(currentRoutine.getTitle());
            AppData.serialize(loader.getDirectory(), currentRoutine);
        }
    }

    public Routine getCurrentRoutineObject() {
        String title = this.title.getText();
        String[][] tasks = new String[24][7];
        String[][] backgroundColors = new String[24][7];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                tasks[j][i] = routineTextFields[j][i].getText();
                backgroundColors[j][i] = routineBackgroundColors[j][i];
            }
        }

        return new Routine(title, tasks, backgroundColors);
    }

    @FXML
    private void closeRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        Routine currentRoutine = getCurrentRoutineObject();
        Routine savedRoutine = AppData.deserialize(loader.getDirectory());

        if (!savedRoutine.equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());

            if (result.isPresent()) {
                if (result.get().equals(exitWithSaving)) {
                    exitWithSavingRoutine(currentRoutine);
                }

                if (result.get().equals(exitWithoutSaving)) {
                    exitWithoutSavingRoutine();
                }
            }
        } else {
            exitWithoutSavingRoutine();
        }
    }

    public void closeRoutineOnCloseRequest(WindowEvent event) throws IOException, ClassNotFoundException {
        Routine currentRoutine = getCurrentRoutineObject();
        Routine savedRoutine = AppData.deserialize(loader.getDirectory());

        if (!savedRoutine.equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());

            if (result.isPresent()) {
                if (result.get().equals(exitWithSaving)) {
                    exitWithSavingRoutine(currentRoutine);
                }

                if (result.get().equals(exitWithoutSaving)) {
                    exitWithoutSavingRoutine();
                }

                if (result.get().equals(cancel)) {
                    event.consume();
                }
            }
        } else {
            exitWithoutSavingRoutine();
        }
    }

    private static Optional<ButtonType> showUnsavedChangesAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("RoutineBean");
        alert.getButtonTypes().clear();

        alert.getDialogPane().setId("alertPane");
        alert.getDialogPane().getStylesheets().add(AppUtils.STYLESHEET);
        alert.getDialogPane().setContentText("Do you want to save changes to " + title + "?");

        alert.getButtonTypes().add(exitWithSaving);
        alert.getButtonTypes().add(exitWithoutSaving);
        alert.getButtonTypes().add(cancel);

        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(AppUtils.ICON);

        return alert.showAndWait();
    }

    private void exitWithSavingRoutine(Routine routine) throws IOException {
        loader.setRoutine(routine);
        loader.getButton().setText(routine.getTitle());

        AppData.serialize(loader.getDirectory(), routine);
        AppUtils.writeRoutineProperties(properties, stage);

        stage.close();
        loader.getButton().setDisable(false);
    }

    private void exitWithoutSavingRoutine() {
        AppUtils.writeRoutineProperties(properties, stage);

        stage.close();
        loader.getButton().setDisable(false);
    }

    @FXML
    private void openExplorer(ActionEvent event) {
        AppUtils.openDirectory(AppUtils.createRoutineFile(loader.getDirectory()));
    }

    @FXML
    private void exportToCSV(ActionEvent event) {

    }

    @FXML
    private void quitProgram(ActionEvent event) throws IOException, ClassNotFoundException {
        Routine currentRoutine = getCurrentRoutineObject();
        Routine savedRoutine = AppData.deserialize(loader.getDirectory());

        if (!savedRoutine.equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());
            if (result.isPresent()) {
                if (result.get().equals(exitWithSaving)) {
                    exitWithSavingApp(currentRoutine);
                }

                if (result.get().equals(exitWithoutSaving)) {
                    exitWithoutSavingApp();
                }
            }
        } else {
            exitWithoutSavingApp();
        }
    }

    private void exitWithSavingApp(Routine routine) throws IOException {
        loader.setRoutine(routine);
        loader.getButton().setText(routine.getTitle());

        AppData.serialize(loader.getDirectory(), routine);
        AppUtils.writeRoutineProperties(properties, stage);
        Platform.exit();
    }

    private void exitWithoutSavingApp() {
        AppUtils.writeRoutineProperties(properties, stage);
        Platform.exit();
    }

    public void loadRoutine(Routine routine) {
        title.setText(routine.getTitle());
        titleTextField.setText(routine.getTitle());

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                routineBackgroundColors[j][i] = routine.getBackgroundColors()[j][i];

                routineTextFields[j][i].setStyle("-fx-background-color: " +
                        routineBackgroundColors[j][i] + "; -fx-border-color: black;");

                routineTextFields[j][i].setText(routine.getTasks()[j][i]);
            }
        }
    }

    @FXML
    private void undoChange(ActionEvent event) {
        if(currentRoutineIndex > 1) {
            currentRoutineIndex--;
            loadRoutine(originator.restoreFromMemento(caretaker.getMemento(currentRoutineIndex - 1)));
            redoButton.setDisable(false);
            undoButton.setDisable(currentRoutineIndex <= 1);
        } else {
            undoButton.setDisable(true);
        }
    }

    @FXML
    private void redoChange(ActionEvent event) {
        if(savedRoutinesIndex > currentRoutineIndex) {
            currentRoutineIndex++;
            Routine routine = originator.restoreFromMemento(caretaker.getMemento(currentRoutineIndex - 1));
            loadRoutine(routine);
            undoButton.setDisable(false);
            redoButton.setDisable(savedRoutinesIndex <= currentRoutineIndex);
        } else {
            redoButton.setDisable(true);
        }
    }

    private void updateMemento() {
        if (!(caretaker.getMemento(currentRoutineIndex - 1).getSavedRoutine().equals(getCurrentRoutineObject()))) {

            originator.set(getCurrentRoutineObject());

            if (currentRoutineIndex == savedRoutinesIndex){
                caretaker.addMemento(originator.storeInMemento());
            } else if (currentRoutineIndex < savedRoutinesIndex) {
                caretaker.clearMemento(currentRoutineIndex);
                caretaker.addMemento(originator.storeInMemento());
                savedRoutinesIndex = currentRoutineIndex;
            }

            savedRoutinesIndex++;
            currentRoutineIndex++;
            undoButton.setDisable(false);
        }
    }

    @FXML
    private void setTitle(KeyEvent event) {
        title.setText(titleTextField.getText());
    }

    @FXML
    private void updateRoutine(ActionEvent event) {

    }

    @FXML
    private void clearRoutine(ActionEvent event) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                routineTextFields[j][i].setText("");
                routineBackgroundColors[j][i] = ColorUtils.colorToRGBA(Color.WHITE);
            }
        }

        loadRoutine(getCurrentRoutineObject());
        updateMemento();
    }

    @FXML
    private void openAboutBox(ActionEvent event) throws IOException {
        AppUtils.openAboutBox();
    }

    @FXML
    private void checkForUpdate(ActionEvent event) throws IOException {
        if (UpdateManager.isUpdateAvailable()) {
            UpdateManager.showUpdateDialog();
        }
    }

    public void initializeMemento() {
        Routine routine = getCurrentRoutineObject();
        originator.set(routine);
        caretaker.addMemento(originator.storeInMemento());
        savedRoutinesIndex++;
        currentRoutineIndex++;
    }

    private void setTaskInputs(int row) {
        for (int i = 0; i < 7; i++) {
            taskTextFields[i].setText(routineTextFields[row][i].getText());
            taskColorPickers[i].setValue(ColorUtils.RGBAToColor(routineBackgroundColors[row][i]));
        }
    }

    private void duplicateTask(int index, KeyEvent event) {
        if (new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN).match(event) && index < 6) {
            taskTextFields[index + 1].setText(taskTextFields[index].getText());
            taskColorPickers[index + 1].setValue(taskColorPickers[index].getValue());

            Platform.runLater(taskTextFields[index + 1]::requestFocus);
        }

        if (new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event) && index > 0) {
            taskTextFields[index - 1].setText(taskTextFields[index].getText());
            taskColorPickers[index - 1].setValue(taskColorPickers[index].getValue());

            Platform.runLater(taskTextFields[index - 1]::requestFocus);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                TextField textField = new TextField();
                textField.setPrefSize(166, 36);
                textField.setAlignment(Pos.CENTER);
                textField.setFont(new Font("Segoe UI",18));
                textField.setStyle("-fx-border-color: black");

                textField.setOnKeyReleased(e -> updateMemento());

                routineTextFields[j][i] = textField;
                routineGrid.add(textField,i+1,j+1);
            }
        }

        taskTextFields[0] = mondayTextField;
        taskTextFields[1] = tuesdayTextField;
        taskTextFields[2] = wednesdayTextField;
        taskTextFields[3] = thursdayTextField;
        taskTextFields[4] = fridayTextField;
        taskTextFields[5] = saturdayTextField;
        taskTextFields[6] = sundayTextField;

        taskColorPickers[0] = mondayColorPicker;
        taskColorPickers[1] = tuesdayColorPicker;
        taskColorPickers[2] = wednesdayColorPicker;
        taskColorPickers[3] = thursdayColorPicker;
        taskColorPickers[4] = fridayColorPicker;
        taskColorPickers[5] = saturdayColorPicker;
        taskColorPickers[6] = sundayColorPicker;

        for (int i = 0; i < 7; i++) {
            int index = i;

            taskTextFields[index].setOnKeyPressed(event -> duplicateTask(index, event));
            taskColorPickers[index].setOnKeyPressed(event -> duplicateTask(index, event));
        }

        for (int i = 0; i < 24; i++) {
            int index = i;
            routineGrid.getChildren().get(i + 8).setOnMouseClicked(event -> {
                setTaskInputs(index);
            });
        }

        saveButton.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        closeButton.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        undoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        redoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));

        undoButton.setDisable(true);
        redoButton.setDisable(true);
    }
}