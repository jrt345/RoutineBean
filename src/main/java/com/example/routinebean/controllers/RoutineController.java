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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoutineController implements Initializable {

    private static final ButtonType EXIT_WITH_SAVING = new ButtonType("Save");
    private static final ButtonType EXIT_WITHOUT_SAVING = new ButtonType("Don't Save");
    private static final ButtonType CANCEL = new ButtonType("Cancel");

    private final TextField[][] routineTextFields = new TextField[24][7];
    private final String[][] routineBackgroundColors = new String[24][7];

    private final String[] stringsHours = new String[24];
    private final ToggleButton[] dayToggleButtons = new ToggleButton[7];

    private final Originator originator = new Originator();
    private final Caretaker caretaker = new Caretaker();

    private int savedRoutinesIndex = 0;
    private int currentRoutineIndex = 0;

    private RoutineLoader loader;
    private Stage stage;
    private RoutineProperties properties;

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
    private TextField taskTextField;

    @FXML
    private ColorPicker taskColorPicker;

    @FXML
    private ChoiceBox<String> firstHour;

    @FXML
    private ChoiceBox<String> secondHour;

    @FXML
    private SplitMenuButton addDeleteButton;

    @FXML
    private RadioMenuItem addMenuItem;

    @FXML
    private RadioMenuItem deleteMenuItem;

    @FXML
    private HBox daysHBox;

    @FXML
    private GridPane routineGrid;

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

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                tasks[i][j] = routineTextFields[i][j].getText();
                backgroundColors[i][j] = routineBackgroundColors[i][j];
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
                if (result.get().equals(EXIT_WITH_SAVING)) {
                    exitWithSavingRoutine(currentRoutine);
                }

                if (result.get().equals(EXIT_WITHOUT_SAVING)) {
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
                if (result.get().equals(EXIT_WITH_SAVING)) {
                    exitWithSavingRoutine(currentRoutine);
                }

                if (result.get().equals(EXIT_WITHOUT_SAVING)) {
                    exitWithoutSavingRoutine();
                }

                if (result.get().equals(CANCEL)) {
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

        alert.getButtonTypes().add(EXIT_WITH_SAVING);
        alert.getButtonTypes().add(EXIT_WITHOUT_SAVING);
        alert.getButtonTypes().add(CANCEL);

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
                if (result.get().equals(EXIT_WITH_SAVING)) {
                    exitWithSavingApp(currentRoutine);
                }

                if (result.get().equals(EXIT_WITHOUT_SAVING)) {
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

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                routineBackgroundColors[i][j] = routine.getBackgroundColors()[i][j];
                routineTextFields[i][j].setText(routine.getTasks()[i][j]);
                setTextFieldBackgroundColor(routineTextFields[i][j], routineBackgroundColors[i][j]);
            }
        }
    }

    private void setTextFieldBackgroundColor(TextField textField, String backgroundColor) {
        textField.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black;");
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
    private void clearRoutine(ActionEvent event) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                routineTextFields[i][j].setText("");
                routineBackgroundColors[i][j] = ColorUtils.colorToRGBA(Color.WHITE);
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

    @FXML
    private void setTitle(KeyEvent event) {
        title.setText(titleTextField.getText());
    }

    @FXML
    public void switchToAdd(ActionEvent event) {
        addDeleteButton.setText("Add");
    }

    @FXML
    public void switchToDelete(ActionEvent event) {
        addDeleteButton.setText("Delete");
    }

    @FXML
    public void updateRoutineTasks(ActionEvent event) {
        String task = taskTextField.getText();
        String color = ColorUtils.colorToRGBA(taskColorPicker.getValue());

        if (!addMenuItem.isSelected() && deleteMenuItem.isSelected()) {
            task = "";
            color = ColorUtils.colorToRGBA(Color.WHITE);
        }

        for (int i : getSelectedHours()) {
            for (int j : getSelectedDays()) {
                routineTextFields[i][j].setText(task);
                routineBackgroundColors[i][j] = color;
                setTextFieldBackgroundColor(routineTextFields[i][j], routineBackgroundColors[i][j]);
            }
        }

        updateMemento();
    }

    private int[] getSelectedDays() {
        ArrayList<Integer> dayIndices = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (dayToggleButtons[i].isSelected()) {
                dayIndices.add(i);
            }
        }

        return dayIndices.stream().mapToInt(i->i).toArray();
    }

    private int[] getSelectedHours() {
        Integer start = getHourIndex(firstHour.getValue());
        Integer end = getHourIndex(secondHour.getValue());

        ArrayList<Integer> hourIndices = new ArrayList<>();
        if (start != null && end != null) {
            if (start <= end) {
                addHourRange(hourIndices, start, end);
            }

            if (start > end) {
                addHourRange(hourIndices, 0, end);
                addHourRange(hourIndices, start, 23);
            }
        }

        return hourIndices.stream().mapToInt(i->i).toArray();
    }

    private Integer getHourIndex(String value) {
        Integer index = null;

        for (int i = 0; i < 24; i++) {
            if (stringsHours[i].equals(value)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private void addHourRange(ArrayList<Integer> hourIndices, int start, int end) {
        for (int i = 0; i < (end - start + 1); i++) {
            hourIndices.add(i + start);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                TextField textField = new TextField();
                textField.setPrefSize(166, 36);
                textField.setAlignment(Pos.CENTER);
                textField.setFont(new Font("Segoe UI",18));
                textField.setStyle("-fx-border-color: black");

                textField.setOnKeyReleased(e -> updateMemento());

                routineTextFields[i][j] = textField;
                routineGrid.add(textField,j+1,i+1);
            }
        }

        for (int i = 0; i < 24; i++) {
            stringsHours[i] = ((Label) routineGrid.getChildren().get(i + 8)).getText();
        }

        firstHour.setItems(FXCollections.observableList(Arrays.asList(stringsHours)));
        secondHour.setItems(FXCollections.observableList(Arrays.asList(stringsHours)));

        for (int i = 0; i < 7; i++) {
            dayToggleButtons[i] = (ToggleButton) daysHBox.getChildren().get(i);
        }

        saveButton.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        closeButton.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        undoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        redoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));

        undoButton.setDisable(true);
        redoButton.setDisable(true);
    }

    public void setLoader(RoutineLoader loader) {
        this.loader = loader;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProperties(RoutineProperties properties) {
        this.properties = properties;
    }
}