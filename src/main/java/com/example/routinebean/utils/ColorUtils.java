package com.example.routinebean.utils;

import javafx.scene.paint.Color;

public class ColorUtils {

    private static int RGBToInt(double val) {
        return (int) (Math.round((val)*255));
    }

    public static String colorToRGBA(Color color) {
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();
        double opacity = color.getOpacity();

        return "rgba(" + RGBToInt(red) + ","+ RGBToInt(green) + ","+ RGBToInt(blue) + ","+ opacity + ")";
    }
}
