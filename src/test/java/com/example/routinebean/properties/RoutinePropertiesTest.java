package com.example.routinebean.properties;

import com.example.routinebean.data.Routine;
import com.example.routinebean.utils.AppUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test @DisplayName("Load, empty RoutineProperties optional")
    void loadEmptyRoutineProperties() {
        assertEquals(Optional.empty(), RoutineProperties.load(null));
        assertEquals(Optional.empty(), RoutineProperties.load("</>"));
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