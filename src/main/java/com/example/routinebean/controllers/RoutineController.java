package com.example.routinebean.controllers;

import com.example.routinebean.utils.*;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RoutineController implements Initializable {

    private Routine routine;

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    private String routineFolderName;

    public String getFolderName() {
        return routineFolderName;
    }

    public void setFolderName(String folderName) {
        this.routineFolderName = folderName;
    }

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Label title;

    @FXML
    private MenuItem newRoutineButton;

    @FXML
    private MenuItem openButton;

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


    private final TextField[][] textFieldArray = new TextField[24][7];

    @FXML
    private void createRoutine(ActionEvent event) throws IOException {
        AppProperties.saveProperties(stage, false);
        AppUtils.newRoutine();
    }

    @FXML
    private void openRoutine(ActionEvent event) throws IOException, ClassNotFoundException {
        AppProperties.saveProperties(stage, false);
        AppUtils.openRoutine(stage);
    }

    @FXML
    private void saveRoutine(ActionEvent event) throws IOException {
        Routine routine = new Routine();
        routine.setTitle(title.getText());
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                routine.getTasks()[j][i] = textFieldArray[j][i].getText();
                routine.getBackgroundColors()[j][i] = this.routine.getBackgroundColors()[j][i];
            }
        }
        AppData.serialize(routineFolderName, routine);
    }

    @FXML
    private void closeRoutine(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void quitProgram(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void setTitle(ActionEvent event) {
        title.setText(titleTextField.getText());
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

    private int dayToNumber(String dayOfTheWeek) {
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

    private int timeToNumber(String timeOfDay) {
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
        if (day1 == 0){
            day1 = day2;
        }
        if (day2 == 0){
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
        if (time1 == 0){
            time1 = time2;
        }
        if (time2 == 0){
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
                    textFieldArray[i - 1][day - 1].setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black;");
                    routine.getBackgroundColors()[i - 1][day - 1] = backgroundColor;
                }
            }
        }
    }

    @FXML
    private void addTasks(ActionEvent event) {
        modifyTasks(false);
    }

    @FXML
    private void deleteTasks(ActionEvent event) {
        modifyTasks(true);
    }

    public void loadRoutine() {
        title.setText(routine.getTitle());

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 24; j++) {
                textFieldArray[j][i].setStyle("-fx-background-color: " +
                        routine.getBackgroundColors()[j][i] +
                        "; -fx-border-color: black;");
                textFieldArray[j][i].setText(routine.getTasks()[j][i]);
            }
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
                textFieldArray[j][i] = textField;
                routineGrid.add(textField,i+1,j+1);
            }
        }

        newRoutineButton.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        openButton.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
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
    }
}
