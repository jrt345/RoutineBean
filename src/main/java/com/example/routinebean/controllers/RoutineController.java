package com.example.routinebean.controllers;

import com.example.routinebean.commands.Caretaker;
import com.example.routinebean.commands.Originator;
import com.example.routinebean.data.Routine;
import com.example.routinebean.data.TaskPreset;
import com.example.routinebean.properties.RoutineProperties;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.ColorUtils;
import com.example.routinebean.utils.UpdateManager;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoutineController implements Initializable {

    private static final ButtonType EXIT_WITH_SAVING = new ButtonType("Save");
    private static final ButtonType EXIT_WITHOUT_SAVING = new ButtonType("Don't Save");
    private static final ButtonType CANCEL = new ButtonType("Cancel");

    private final RoutineTextField[][] routineTextFields = new RoutineTextField[24][7];

    private final String[] stringsHours = new String[24];
    private final String[] stringsDays = new String[7];
    private final ToggleButton[] dayToggleButtons = new ToggleButton[7];

    private final ArrayList<TaskPreset> taskPresetArrayList = new ArrayList<>();

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
    private HBox taskPresetHBox;

    @FXML
    private GridPane routineGrid;

    @FXML
    private void saveRoutine(ActionEvent event) throws IOException {
        Routine currentRoutine = getCurrentRoutineObject();
        Optional<Routine> savedRoutine = Routine.deserialize(loader.getDirectory());

        if (savedRoutine.isPresent() && !savedRoutine.get().equals(currentRoutine) || savedRoutine.isEmpty()) {
            saveRoutine(currentRoutine);
        }
    }

    private void saveRoutine(Routine currentRoutine) throws IOException {
        loader.setRoutine(currentRoutine);
        loader.getButton().setText(currentRoutine.getTitle());
        Routine.serialize(loader.getDirectory(), currentRoutine);
    }

    public Routine getCurrentRoutineObject() {
        String title = this.title.getText();
        String[][] tasks = new String[24][7];
        String[][] backgroundColors = new String[24][7];

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                tasks[i][j] = routineTextFields[i][j].getText();
                backgroundColors[i][j] = ColorUtils.colorToRgba(routineTextFields[i][j].backgroundColor);
            }
        }

        return new Routine(title, tasks, backgroundColors);
    }

    @FXML
    private void closeRoutine(ActionEvent event) throws IOException {
        Routine currentRoutine = getCurrentRoutineObject();
        Optional<Routine> savedRoutine = Routine.deserialize(loader.getDirectory());

        if (savedRoutine.isPresent() && !savedRoutine.get().equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());

            if (result.isPresent() && result.get().equals(EXIT_WITH_SAVING)) {
                exitWithSavingRoutine(currentRoutine);
            }

            if (result.isPresent() && result.get().equals(EXIT_WITHOUT_SAVING)) {
                exitWithoutSavingRoutine();
            }

        } else if (savedRoutine.isEmpty()) {
            exitWithSavingRoutine(currentRoutine);
        } else {
            exitWithoutSavingRoutine();
        }
    }

    public void closeRoutineOnCloseRequest(WindowEvent event) throws IOException {
        Routine currentRoutine = getCurrentRoutineObject();
        Optional<Routine> savedRoutine = Routine.deserialize(loader.getDirectory());

        if (savedRoutine.isPresent() && !savedRoutine.get().equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());

            if (result.isPresent() && result.get().equals(EXIT_WITH_SAVING)) {
                exitWithSavingRoutine(currentRoutine);
            }

            if (result.isPresent() && result.get().equals(EXIT_WITHOUT_SAVING)) {
                exitWithoutSavingRoutine();
            }

            if (result.isPresent() && result.get().equals(CANCEL)) {
                event.consume();
            }

        } else if (savedRoutine.isEmpty()) {
            exitWithSavingRoutine(currentRoutine);
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
        saveRoutine(routine);
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
    private void exportToCSV(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(AppUtils.ROUTINES_DIRECTORY);
        fileChooser.setInitialFileName(getCurrentRoutineObject().getTitle().concat(".csv"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("(CSV) Comma Delimited", "*.csv*"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.write(routineToStringCSV());
            }
        }
    }

    private String routineToStringCSV() {
        StringBuilder sb = new StringBuilder();

        sb.append(((Label) routineGrid.getChildren().get(0)).getText());

        for (String stringDay : stringsDays) {
            sb.append(",");
            sb.append(stringDay);
        }

        sb.append("\n");

        for (int i = 0; i < 24; i++) {
            sb.append(stringsHours[i]);

            for (int j = 0; j < 7; j++) {
                sb.append(",");
                sb.append(routineTextFields[i][j].getText());
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    @FXML
    private void quitProgram(ActionEvent event) throws IOException {
        Routine currentRoutine = getCurrentRoutineObject();
        Optional<Routine> savedRoutine = Routine.deserialize(loader.getDirectory());

        if (savedRoutine.isPresent() && !savedRoutine.get().equals(currentRoutine)) {
            Optional<ButtonType> result = showUnsavedChangesAlert(title.getText());

            if (result.isPresent() && result.get().equals(EXIT_WITH_SAVING)) {
                exitWithSavingApp(currentRoutine);
            }

            if (result.isPresent() && result.get().equals(EXIT_WITHOUT_SAVING)) {
                exitWithoutSavingApp();
            }

        } else if (savedRoutine.isEmpty()) {
            exitWithSavingApp(currentRoutine);
        } else {
            exitWithoutSavingApp();
        }
    }

    private void exitWithSavingApp(Routine routine) throws IOException {
        saveRoutine(routine);
        AppUtils.writeRoutineProperties(properties, stage);
        Platform.exit();
    }

    private void exitWithoutSavingApp() {
        AppUtils.writeRoutineProperties(properties, stage);
        Platform.exit();
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

    public void loadRoutine(Routine routine) {
        title.setText(routine.getTitle());
        titleTextField.setText(routine.getTitle());

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                routineTextFields[i][j].setText(routine.getTasks()[i][j]);
                routineTextFields[i][j].setBackgroundColor(ColorUtils.rgbaToColor(routine.getBackgroundColors()[i][j]));
            }
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

    public void initializeMemento() {
        Routine routine = getCurrentRoutineObject();
        originator.set(routine);
        caretaker.addMemento(originator.storeInMemento());
        savedRoutinesIndex++;
        currentRoutineIndex++;
    }

    @FXML
    private void clearRoutine(ActionEvent event) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                routineTextFields[i][j].setText("");
                routineTextFields[i][j].setBackgroundColor(Color.WHITE);
            }
        }

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

    @FXML
    private void setTitle(KeyEvent event) {
        title.setText(titleTextField.getText());
    }

    @FXML
    private void saveTaskPreset(ActionEvent event) throws IOException {
        TaskPreset taskPreset = new TaskPreset(taskTextField.getText(), ColorUtils.colorToRgba(taskColorPicker.getValue()));

        if (isNewTaskPreset(taskPreset)) {
            taskPresetHBox.getChildren().add(generateTaskPresetButton(taskPreset));
            taskPresetArrayList.add(taskPreset);
            TaskPreset.toJson(loader.getDirectory(), taskPresetArrayList);
        }
    }

    private boolean isNewTaskPreset(TaskPreset taskPreset) {
        ObservableList<Node> taskPresetHBoxNodes = taskPresetHBox.getChildren();

        for (Node taskPresetHBoxNode : taskPresetHBoxNodes) {
            if (((TaskPresetButton) taskPresetHBoxNode).taskPreset.equals(taskPreset)) {
                return false;
            }
        }

        return true;
    }

    private TaskPresetButton generateTaskPresetButton(TaskPreset taskPreset) {
        TaskPresetButton taskPresetButton = new TaskPresetButton(taskPreset);

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            try {
                deleteTaskPreset(taskPresetButton);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        taskPresetButton.setContextMenu(new ContextMenu(delete));
        taskPresetButton.setOnMouseClicked(event -> {
            taskTextField.setText(taskPreset.getName());
            taskColorPicker.setValue(ColorUtils.rgbaToColor(taskPreset.getColor()));
        });


        return taskPresetButton;
    }

    private void deleteTaskPreset(TaskPresetButton taskPresetButton) throws IOException {
        taskPresetHBox.getChildren().remove(taskPresetButton);
        taskPresetArrayList.remove(taskPresetButton.taskPreset);

        TaskPreset.toJson(loader.getDirectory(), taskPresetArrayList);
    }

    @FXML
    private void selectSingleHour(ActionEvent event) {
        secondHour.setValue(firstHour.getValue());
    }

    @FXML
    private void selectAllButtons(ActionEvent event) {
        Boolean[] buttonSelected = new Boolean[7];

        for (int i = 0; i < 7; i++) {
            buttonSelected[i] = dayToggleButtons[i].isSelected();
        }

        if (!Arrays.asList(buttonSelected).contains(true) || !Arrays.asList(buttonSelected).contains(false)) {
            for (ToggleButton dayToggleButton : dayToggleButtons) {
                dayToggleButton.fire();
            }
        } else {
            for (ToggleButton dayToggleButton : dayToggleButtons) {
                dayToggleButton.setSelected(true);
            }
        }
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
        Color color = taskColorPicker.getValue();

        if (!addMenuItem.isSelected() && deleteMenuItem.isSelected()) {
            task = "";
            color = Color.WHITE;
        }

        for (int i : getSelectedHours()) {
            for (int j : getSelectedDays()) {
                routineTextFields[i][j].setText(task);
                routineTextFields[i][j].setBackgroundColor(color);
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

    private void setTaskInputs(String task, int time, int day) {
        for (ToggleButton dayToggleButton : dayToggleButtons) {
            dayToggleButton.setSelected(false);
        }

        taskTextField.setText(task);
        taskColorPicker.setValue(routineTextFields[time][day].backgroundColor);
        firstHour.getSelectionModel().select(time);
        secondHour.getSelectionModel().select(time);
        dayToggleButtons[day].setSelected(true);
    }

    public void loadTaskPresetData() {
        try {
            ArrayList<TaskPreset> taskPresetsFromJson = TaskPreset.fromJson(loader.getDirectory());

            for (TaskPreset taskPreset : taskPresetsFromJson) {
                if (isNewTaskPreset(taskPreset)) {
                    taskPresetHBox.getChildren().add(generateTaskPresetButton(taskPreset));
                    taskPresetArrayList.add(taskPreset);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            try {
                TaskPreset.toJson(loader.getDirectory(), taskPresetArrayList);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                RoutineTextField textField = new RoutineTextField();

                ContextMenu contextMenu = new ContextMenu();
                MenuItem delete = new MenuItem("Delete");
                MenuItem saveTask = new MenuItem("Save Task");

                int time = i;
                int day = j;
                delete.setOnAction(event -> {
                    textField.setText("");
                    textField.setBackgroundColor(Color.WHITE);

                    updateMemento();
                });

                saveTask.setOnAction(event -> {
                    TaskPreset taskPreset = new TaskPreset(textField.getText(), ColorUtils.colorToRgba(textField.backgroundColor));

                    if (isNewTaskPreset(taskPreset)) {
                        taskPresetHBox.getChildren().add(generateTaskPresetButton(taskPreset));
                        taskPresetArrayList.add(taskPreset);
                        try {
                            TaskPreset.toJson(loader.getDirectory(), taskPresetArrayList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });


                contextMenu.getItems().setAll(delete, saveTask);
                textField.setContextMenu(contextMenu);

                textField.setOnMouseClicked(event -> setTaskInputs(textField.getText(), time, day));
                textField.setOnKeyPressed(event -> setTaskInputs(textField.getText(), time, day));
                textField.setOnKeyReleased(event -> updateMemento());

                routineTextFields[i][j] = textField;
                routineGrid.add(textField,j+1,i+1);
            }
        }

        for (int i = 0; i < 24; i++) {
            stringsHours[i] = ((Label) routineGrid.getChildren().get(i + 8)).getText();
        }

        firstHour.setItems(FXCollections.observableList(Arrays.asList(stringsHours)));
        firstHour.setValue(stringsHours[0]);
        secondHour.setItems(FXCollections.observableList(Arrays.asList(stringsHours)));
        secondHour.setValue(stringsHours[0]);

        for (int i = 0; i < 7; i++) {
            stringsDays[i] = ((Label) routineGrid.getChildren().get(i + 1)).getText();
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


    private static class RoutineTextField extends TextField {

        private Color backgroundColor = Color.WHITE;

        private RoutineTextField() {
            setPrefSize(166, 36);
            setAlignment(Pos.CENTER);
            setFont(new Font("Segoe UI",18));
            setStyle("-fx-border-color: black");
            setBackgroundColor(Color.WHITE);
        }

        private void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            setStyle("-fx-background-color: " + ColorUtils.colorToRgba(backgroundColor) + "; -fx-border-color: black;");
        }
    }


    private static class TaskPresetButton extends Button {

        private final TaskPreset taskPreset;

        private TaskPresetButton(TaskPreset taskPreset) {
            this.taskPreset = taskPreset;
            setMinWidth(70);
            setPrefHeight(Double.MAX_VALUE);
            setText(taskPreset.getName());
            setStyle("-fx-background-color: " + taskPreset.getColor() + "; -fx-background-radius: 0;");
        }
    }
}