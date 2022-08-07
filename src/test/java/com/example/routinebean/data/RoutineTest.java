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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoutineTest {

    private static Routine routine;
    private static final String DIRECTORY = "JUnit 5 Routine Test";

    @BeforeAll
    static void beforeAll() {
        String[][] tasks = new String[24][7];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                tasks[i][j] = "Test";
            }
        }

        String[][] backgroundColors = new String[24][7];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 7; j++) {
                backgroundColors[i][j] = ColorUtils.colorToRgba(Color.BLUE);
            }
        }

        routine = new Routine("JUnit 5 Routine", tasks, backgroundColors);

        AppUtils.createNewRoutine("JUnit 5 Routine Test", routine);
    }

    @Test
    @DisplayName("Serialize throws NullPointerException")
    void serializeNullArgs() {
        assertThrows(NullPointerException.class, () -> Routine.serialize(null, null));
    }

    @Test
    @DisplayName("Serialize throws IOException")
    void serializeInvalidArgs() {
        assertThrows(IOException.class, () -> Routine.serialize("</>", new Routine()));
    }

    @Test
    @DisplayName("Deserialize, present routine optional")
    void deserializePresentRoutine() {
        Optional<Routine> optionalRoutine = Routine.deserialize(DIRECTORY);

        assertEquals(Optional.of(routine), optionalRoutine);
    }

    @Test
    @DisplayName("Deserialize, empty routine optional")
    void deserializeEmptyRoutine() {
        Optional<Routine> nullDirectoryOptionalRoutine = Routine.deserialize(null);
        assertEquals(Optional.empty(), nullDirectoryOptionalRoutine);

        Optional<Routine> nonExistentDirectoryOptionalRoutine = Routine.deserialize("</>");
        assertEquals(Optional.empty(), nonExistentDirectoryOptionalRoutine);
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