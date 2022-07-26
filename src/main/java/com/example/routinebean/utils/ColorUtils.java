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

    private static double intToRGB(int val) {
        return val/255.0;
    }

    public static Color RGBAToColor(String RGBA) {
        String[] stringsRGBA = RGBA.replaceAll("[a-z()]", "").split(",");
        double[] doublesRGBA = new double[3];

        for (int i = 0; i < 3; i++) {
            doublesRGBA[i] = intToRGB(Integer.parseInt(stringsRGBA[i]));
        }

        return new Color(doublesRGBA[0], doublesRGBA[1], doublesRGBA[2], Double.parseDouble(stringsRGBA[3]));
    }
}
