package com.example.routinebean.utils;

import javafx.scene.paint.Color;

public class ColorUtils {

    private static final String rgbaRegexPattern = "^(rgba\\()" +
            "\\b([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])," +
            "\\b([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])," +
            "\\b([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])," +
            "[0-1]\\.?\\d*\\)$";

    public static boolean isRgbaNotValid(String rgba) {
        if (rgba != null) {
            return !rgba.matches(rgbaRegexPattern);
        }

        return true;
    }

    private static int rgbToInt(double val) {
        return (int) (Math.round((val)*255));
    }

    public static String colorToRgba(Color color) {
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();
        double opacity = color.getOpacity();

        return "rgba(" + rgbToInt(red) + ","+ rgbToInt(green) + ","+ rgbToInt(blue) + ","+ opacity + ")";
    }

    private static double intToRgb(int val) {
        return val/255.0;
    }

    public static Color rgbaToColor(String rgba) {
        String[] stringsRgba = rgba.replaceAll("[a-z()]", "").split(",");
        double[] doublesRgba = new double[3];

        for (int i = 0; i < 3; i++) {
            doublesRgba[i] = intToRgb(Integer.parseInt(stringsRgba[i]));
        }

        return new Color(doublesRgba[0], doublesRgba[1], doublesRgba[2], Double.parseDouble(stringsRgba[3]));
    }
}
