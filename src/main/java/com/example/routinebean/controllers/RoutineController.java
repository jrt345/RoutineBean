package com.example.routinebean.controllers;

import com.example.routinebean.commands.Caretaker;
import com.example.routinebean.commands.Originator;
import com.example.routinebean.utils.AppData;
import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.ColorUtils;
import com.example.routinebean.utils.Routine;
import com.example.routinebean.utils.properties.RoutineProperties;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoutineController implements Initializable {

    private static final ButtonType exitWithSaving = new ButtonType("Save");
    private static final ButtonType exitWithoutSaving = new ButtonType("Don't Save");
    private static final ButtonType cancel = new ButtonType("Cancel");

    private RoutineLoader loader;
    public RoutineLoader getLoader() {
        return loader;
    }
    public void setLoader(RoutineLoader loader) {
        this.loader = loader;
    }

    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private RoutineProperties properties;
    public RoutineProperties getProperties() {
        return properties;
    }
    public void setProperties(RoutineProperties properties) {
        this.properties = properties;
    }

    private final Originator originator = new Originator();
    private final Caretaker caretaker = new Caretaker();

    private int savedRoutines = 0;
    private int currentRoutine = 0;

    private final TextField[][] textFieldArray = new TextField[24][7];
    private final String[][] backgroundColorsArray = new String[24][7];

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

    @FXML
    private TextField taskTextField;

    @FXML
    private ChoiceBox<String> daysOfTheWeekChoiceBox1;

    @FXML
    private ChoiceBox<String> daysOfTheWeekChoiceBox2;

    @FXML
    private ChoiceBox<String> timeChoiceBox1;

    @FXML
    private ChoiceBox<String> timeChoiceBox2;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private Label changesSaved;

    @FXML
    private void saveRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        Routine currentRoutine = getCurrentRoutineObject();
        Routine savedRoutine = AppData.deserialize(loader.getDirectory());

        if (!savedRoutine.equals(currentRoutine)){
            loader.setRoutine(currentRoutine);
            loader.getButton().setText(currentRoutine.getTitle());
            AppData.serialize(loader.getDirectory(), currentRoutine);

            changesSaved.setOpacity(1.0);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.5), changesSaved);
            fadeTransition.setToValue(0.0);
            fadeTransition.play();
        }
    }

    public Routine getCurrentRoutineObject() {
        String title = this.title.getText();
        String[][] tasks = new String[24][7];
        String[][] backgroundColors = new String[24][7];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                tasks[j][i] = textFieldArray[j][i].getText();
                backgroundColors[j][i] = backgroundColorsArray[j][i];
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
    private void openExplorer(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select," + AppData.ROUTINE_DIRECTORY.concat(loader.getDirectory()) + "\\routine.dat");
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
                backgroundColorsArray[j][i] = routine.getBackgroundColors()[j][i];

                textFieldArray[j][i].setStyle("-fx-background-color: " +
                        backgroundColorsArray[j][i] + "; -fx-border-color: black;");

                textFieldArray[j][i].setText(routine.getTasks()[j][i]);
            }
        }
    }

    @FXML
    private void undoChange(ActionEvent event) {
        if(currentRoutine > 1) {
            currentRoutine--;
            loadRoutine(originator.restoreFromMemento(caretaker.getMemento(currentRoutine - 1)));
            redoButton.setDisable(false);
            undoButton.setDisable(currentRoutine <= 1);
        } else {
            undoButton.setDisable(true);
        }
    }

    @FXML
    private void redoChange(ActionEvent event) {
        if(savedRoutines > currentRoutine) {
            currentRoutine++;
            Routine routine = originator.restoreFromMemento(caretaker.getMemento(currentRoutine - 1));
            loadRoutine(routine);
            undoButton.setDisable(false);
            redoButton.setDisable(savedRoutines <= currentRoutine);
        } else {
            redoButton.setDisable(true);
        }
    }

    private void updateMemento() {
        if (!(caretaker.getMemento(currentRoutine - 1).getSavedRoutine().equals(getCurrentRoutineObject()))) {

            originator.set(getCurrentRoutineObject());

            if (currentRoutine == savedRoutines){
                caretaker.addMemento(originator.storeInMemento());
            } else if (currentRoutine < savedRoutines) {
                caretaker.clearMemento(currentRoutine);
                caretaker.addMemento(originator.storeInMemento());
                savedRoutines = currentRoutine;
            }

            savedRoutines++;
            currentRoutine++;
            undoButton.setDisable(false);
        }
    }

    @FXML
    private void setTitle(ActionEvent event) {
        title.setText(titleTextField.getText());
        updateMemento();
    }

    @FXML
    private void clearSelections(ActionEvent event) {
        taskTextField.setText("");
        daysOfTheWeekChoiceBox1.setValue("(None)");
        daysOfTheWeekChoiceBox2.setValue("(None)");
        timeChoiceBox1.setValue("(None)");
        timeChoiceBox2.setValue("(None)");
        backgroundColorPicker.setValue(Color.WHITE);
    }

    private static int dayToNumber(String dayOfTheWeek) {
        return switch (dayOfTheWeek) {
            case "Monday" -> 1;
            case "Tuesday" -> 2;
            case "Wednesday" -> 3;
            case "Thursday" -> 4;
            case "Friday" -> 5;
            case "Saturday" -> 6;
            case "Sunday" -> 7;
            default -> 0;
        };
    }

    private static int timeToNumber(String timeOfDay) {
        return switch (timeOfDay) {
            case "12:00 am" -> 1;
            case "1:00 am" -> 2;
            case "2:00 am" -> 3;
            case "3:00 am" -> 4;
            case "4:00 am" -> 5;
            case "5:00 am" -> 6;
            case "6:00 am" -> 7;
            case "7:00 am" -> 8;
            case "8:00 am" -> 9;
            case "9:00 am" -> 10;
            case "10:00 am" -> 11;
            case "11:00 am" -> 12;
            case "12:00 pm" -> 13;
            case "1:00 pm" -> 14;
            case "2:00 pm" -> 15;
            case "3:00 pm" -> 16;
            case "4:00 pm" -> 17;
            case "5:00 pm" -> 18;
            case "6:00 pm" -> 19;
            case "7:00 pm" -> 20;
            case "8:00 pm" -> 21;
            case "9:00 pm" -> 22;
            case "10:00 pm" -> 23;
            case "11:00 pm" -> 24;
            default -> 0;
        };
    }

    private int[] selectedDays(int day1, int day2) {
        if (day1 == 0) {
            day1 = day2;
        }
        if (day2 == 0) {
            day2 = day1;
        }

        int dif = day2 - day1;

        int[] selectedDays = null;

        if (dif < 0) {
            int daysTillSunday = (8 - day1);

            selectedDays = new int[daysTillSunday + day2];

            for (int i = 0; i < daysTillSunday; i++) {
                selectedDays[i] = day1 + i;
            }

            for (int i = 0; i < day2; i++) {
                selectedDays[daysTillSunday + i] = i + 1;
            }
        }

        if (dif >= 0) {
            selectedDays = new int[dif + 1];

            for (int i = 0; i < dif + 1; i++) {
                selectedDays[i] = day1 + i;
            }
        }

        return selectedDays;
    }

    private int[] selectedTime(int time1, int time2) {
        if (time1 == 0) {
            time1 = time2;
        }
        if (time2 == 0) {
            time2 = time1;
        }

        int dif = time2 - time1;

        int[] selectedTime = null;

        if (dif < 0) {
            int timeTillMidnight = (25 - time1);

            selectedTime = new int[timeTillMidnight + time2];

            for (int i = 0; i < timeTillMidnight; i++) {
                selectedTime[i] = time1 + i;
            }

            for (int i = 0; i < time2; i++) {
                selectedTime[timeTillMidnight + i] = i + 1;
            }
        }

        if (dif >= 0) {
            selectedTime = new int[dif + 1];

            for (int i = 0; i < dif + 1; i++) {
                selectedTime[i] = time1 + i;
            }
        }

        return selectedTime;
    }

    private void modifyTasks(boolean delete) {
        int day1 = dayToNumber(daysOfTheWeekChoiceBox1.getValue());
        int day2 = dayToNumber(daysOfTheWeekChoiceBox2.getValue());
        int time1 = timeToNumber(timeChoiceBox1.getValue());
        int time2 = timeToNumber(timeChoiceBox2.getValue());

        String task;
        String backgroundColor;

        if (delete) {
            task = "";
            backgroundColor = ColorUtils.colorToRGBA(Color.WHITE);
        } else {
            task = taskTextField.getText();
            backgroundColor = ColorUtils.colorToRGBA(backgroundColorPicker.getValue());
        }

        //If day and time have at least one selected value, set the tasks
        if (!(day1 == 0 && day2 == 0) && !(time1 == 0 && time2 == 0)){
            int[] days = selectedDays(day1, day2);
            int[] time = selectedTime(time1, time2);

            for (int day : days) {
                for (int i : time) {
                    textFieldArray[i - 1][day - 1].setText(task);
                    backgroundColorsArray[i - 1][day - 1] = backgroundColor;
                }
            }

            loadRoutine(getCurrentRoutineObject());
        }

        updateMemento();
    }

    @FXML
    private void addTasks(ActionEvent event) {
        modifyTasks(false);
    }

    @FXML
    private void deleteTasks(ActionEvent event) {
        modifyTasks(true);
    }

    @FXML
    private void clearRoutine(ActionEvent event) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                textFieldArray[j][i].setText("");
                backgroundColorsArray[j][i] = ColorUtils.colorToRGBA(Color.WHITE);
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
        AppUtils.checkForUpdate();
    }

    public void initializeMemento() {
        Routine routine = getCurrentRoutineObject();
        originator.set(routine);
        caretaker.addMemento(originator.storeInMemento());
        savedRoutines++;
        currentRoutine++;
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

                textFieldArray[j][i] = textField;
                routineGrid.add(textField,i+1,j+1);
            }
        }

        saveButton.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        closeButton.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        undoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        redoButton.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));

        daysOfTheWeekChoiceBox1.getItems().add("(None)");
        daysOfTheWeekChoiceBox1.getItems().add("Monday");
        daysOfTheWeekChoiceBox1.getItems().add("Tuesday");
        daysOfTheWeekChoiceBox1.getItems().add("Wednesday");
        daysOfTheWeekChoiceBox1.getItems().add("Thursday");
        daysOfTheWeekChoiceBox1.getItems().add("Friday");
        daysOfTheWeekChoiceBox1.getItems().add("Saturday");
        daysOfTheWeekChoiceBox1.getItems().add("Sunday");

        daysOfTheWeekChoiceBox2.setItems(daysOfTheWeekChoiceBox1.getItems());

        timeChoiceBox1.getItems().add("(None)");
        timeChoiceBox1.getItems().add("12:00 am");

        for (int i = 1; i < 12; i++) {
            timeChoiceBox1.getItems().add(i + ":00 am");
        }

        timeChoiceBox1.getItems().add("12:00 pm");

        for (int i = 1; i < 12; i++) {
            timeChoiceBox1.getItems().add(i + ":00 pm");
        }

        timeChoiceBox2.setItems(timeChoiceBox1.getItems());

        daysOfTheWeekChoiceBox1.setValue("(None)");
        daysOfTheWeekChoiceBox2.setValue("(None)");
        timeChoiceBox1.setValue("(None)");
        timeChoiceBox2.setValue("(None)");

        changesSaved.setOpacity(0.0);

        undoButton.setDisable(true);
        redoButton.setDisable(true);
    }
}