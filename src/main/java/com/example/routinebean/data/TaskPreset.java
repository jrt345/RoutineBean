package com.example.routinebean.data;

import com.example.routinebean.utils.AppUtils;
import com.example.routinebean.utils.ColorUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TaskPreset {

    private String name;
    private String color;

    public TaskPreset(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private static File jsonDirectory(String directory) {
        return new File(AppUtils.ROUTINES_DIRECTORY, new File(directory, "taskPresets.json").getPath());
    }

    public static void toJson(String directory, ArrayList<TaskPreset> taskPresets) throws IOException {
        if (directory == null || taskPresets == null) {
            throw new NullPointerException();
        }

        try (FileWriter writer = new FileWriter(jsonDirectory(directory))) {
            taskPresets.removeIf(taskPreset -> taskPreset.getName() == null);
            taskPresets.removeIf(taskPreset -> ColorUtils.isRgbaNotValid(taskPreset.color));

            new GsonBuilder().setPrettyPrinting().create().toJson(taskPresets, writer);
        }
    }

    public static ArrayList<TaskPreset> fromJson(String directory) throws IOException, JsonSyntaxException {
        if (directory == null) {
            throw new NullPointerException();
        }

        try (FileReader reader = new FileReader(jsonDirectory(directory))) {
            ArrayList<TaskPreset> taskPresets = new Gson().fromJson(reader, new TypeToken<ArrayList<TaskPreset>>(){}.getType());

            taskPresets.removeIf(taskPreset -> taskPreset.getName() == null);
            taskPresets.removeIf(taskPreset -> ColorUtils.isRgbaNotValid(taskPreset.color));

            return taskPresets;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskPreset that = (TaskPreset) o;

        if (!name.equals(that.name)) return false;
        return color.equals(that.color);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TaskPreset{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
