package com.example.routinebean.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUtilsTest {

    @Test
    @DisplayName("Return Routine Directory File")
    void createRoutineFile() {
        assertEquals(new File(AppUtils.ROUTINES_DIRECTORY, "JUnitTest"), AppUtils.createRoutineFile("JUnitTest"));
    }

    @ParameterizedTest
    @MethodSource("filterFolderNameArgs")
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
}