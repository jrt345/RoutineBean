package com.example.routinebean.utils;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ColorUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"rgba(0,153,153,1.0)", "rgba(153,0,153,1.0)", "rgba(153,153,0,1.0)"})
    @DisplayName("RGBA Strings match Regex pattern \"rgba(###,###,###,#.#)\"")
    void RgbaIsValid(String input) {
        assertFalse(ColorUtils.isRgbaNotValid(input));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"rgba(370,153,153,1.0)", "rgba(-370,153,153,1.0)", "rgba(370,153,153,5.0)"})
    @DisplayName("RGBA Strings don't match Regex pattern \"rgba(###,###,###,#.#)\"")
    void RgbaIsNotValid(String input) {
        assertTrue(ColorUtils.isRgbaNotValid(input));
    }

    @Test
    @DisplayName("Convert JavaFX Color to RGBA String")
    void colorToRgba() {
        assertEquals("rgba(0,153,153,1.0)",
                ColorUtils.colorToRgba(new Color(0.0,0.6,0.6,1.0)));
        assertEquals("rgba(153,0,153,1.0)",
                ColorUtils.colorToRgba(new Color(0.6,0.0,0.6,1.0)));
        assertEquals("rgba(153,153,0,1.0)",
                ColorUtils.colorToRgba(new Color(0.6,0.6,0.0,1.0)));
    }

    @Test
    @DisplayName("Convert RGBA String to JavaFX Color")
    void rgbaToColor() {
        assertEquals(new Color(0.0,0.6,0.6,1.0),
                ColorUtils.rgbaToColor("rgba(0,153,153,1.0)"));
        assertEquals(new Color(0.6,0.0,0.6,1.0),
                ColorUtils.rgbaToColor("rgba(153,0,153,1.0)"));
        assertEquals(new Color(0.6,0.6,0.0,1.0),
                ColorUtils.rgbaToColor("rgba(153,153,0,1.0)"));
    }
}