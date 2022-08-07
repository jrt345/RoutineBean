package com.example.routinebean.data;

import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.ColorUtils;
import javafx.scene.paint.Color;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskPresetTest {

    private static final String DIRECTORY = "JUnit 5 Routine Test";
    private static final ArrayList<TaskPreset> taskPresets = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        AppUtils.createNewRoutine("JUnit 5 Routine Test", new Routine("JUnit 5 Routine"));

        taskPresets.add(new TaskPreset("Breakfast", ColorUtils.colorToRgba(Color.CYAN)));
        taskPresets.add(new TaskPreset("Lunch", ColorUtils.colorToRgba(Color.MAGENTA)));
        taskPresets.add(new TaskPreset("Dinner", ColorUtils.colorToRgba(Color.YELLOW)));

        try {
            TaskPreset.toJson(DIRECTORY, taskPresets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test @DisplayName("toJson throws NullPointerException")
    void serializeToJsonNullArgs() {
        assertThrows(NullPointerException.class, () -> TaskPreset.toJson(null, null));
    }

    @Test @DisplayName("toJson throws IoException")
    void serializeToJsonInvalidArgs() {
        assertThrows(IOException.class, () -> TaskPreset.toJson("</>", new ArrayList<>()));
    }

    @Test @DisplayName("Get TaskPresets from Json")
    void deserializeFromJson() throws IOException {
        assertEquals(taskPresets, TaskPreset.fromJson(DIRECTORY));
    }

    @Test @DisplayName("fromJson throws NullPointerException")
    void deserializeFromNullJson() {
        assertThrows(NullPointerException.class, () -> TaskPreset.fromJson(null));
    }

    @Test @DisplayName("fromJson throws IOException")
    void deserializeFromInvalidJson() {
        assertThrows(IOException.class, () -> TaskPreset.fromJson("</>"));
    }

    @AfterAll
    static void afterAll() {
        try {
            FileUtils.deleteDirectory(AppUtils.createRoutineFile(DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}