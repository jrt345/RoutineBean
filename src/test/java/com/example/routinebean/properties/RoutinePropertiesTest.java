package com.example.routinebean.properties;

import com.example.routinebean.data.Routine;
import com.example.routinebean.utils.AppUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoutinePropertiesTest {

    private static RoutineProperties properties;
    private static final String DIRECTORY = "JUnit 5 Routine Test";

    @BeforeAll
    static void beforeAll() throws IOException {
        AppUtils.createNewRoutine("JUnit 5 Routine Test", new Routine("JUnit 5 Routine"));

        properties = new RoutineProperties(DIRECTORY);
        properties.setSize(1000, 1000);

        RoutineProperties.write(properties);
    }

    @Test @DisplayName("Write RoutineProperties throws NullPointerException")
    void writeNullRoutinePropertiesArgs() {
        assertThrows(NullPointerException.class, () -> RoutineProperties.write(null));
    }

    @Test @DisplayName("Write RoutineProperties throws IOException")
    void writeInvalidRoutinePropertiesArgs() {
        assertThrows(IOException.class, () -> RoutineProperties.write(new RoutineProperties("</>")));
    }

    @Test @DisplayName("Load, present RoutineProperties optional")
    void loadPresentRoutineProperties() {
        assertEquals(Optional.of(properties), RoutineProperties.load(DIRECTORY));
    }

    @ParameterizedTest @NullSource @ValueSource(strings = {"</>"})
    @DisplayName("Load, empty RoutineProperties optional")
    void loadEmptyRoutineProperties(String directory) {
        assertEquals(Optional.empty(), RoutineProperties.load(directory));
        assertEquals(Optional.empty(), RoutineProperties.load(directory));
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