package com.example.routinebean.utils;

import com.example.routinebean.data.Routine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppUtilsTest {

    private static final String DIRECTORY = "JUnit 5 Routine Test";

    @Test @DisplayName("Return Routine Directory File")
    void createRoutineFile() {
        assertEquals(new File(AppUtils.ROUTINES_DIRECTORY, "JUnitTest"), AppUtils.createRoutineFile("JUnitTest"));
    }

    @ParameterizedTest @MethodSource("filterFolderNameArgs")
    @DisplayName("Filter and rename illegal folder names")
    void filterFolderName(String expected, String input) {
        assertEquals(expected, AppUtils.filterFolderName(input));
    }

    private static Stream<Arguments> filterFolderNameArgs() {
        return Stream.of(
                Arguments.of("_CON_", "CON"),
                Arguments.of("____Test____", "<<::Test::>>"),
                Arguments.of("Test_", "Test:     ")
        );
    }

    @Test @DisplayName("Create new Routine directory")
    void createNewRoutine() {
        assertTrue(AppUtils.createNewRoutine(DIRECTORY, new Routine()));
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