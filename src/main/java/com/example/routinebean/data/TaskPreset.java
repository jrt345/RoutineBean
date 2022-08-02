package com.example.routinebean.data;

import com.example.routinebean.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public static File jsonDirectory(String directory) {
        return new File(AppUtils.ROUTINES_DIRECTORY, new File(directory, "taskPresets.json").getPath());
    }

    public static void toJson(String directory, ArrayList<TaskPreset> taskPresets) throws IOException {
        try (FileWriter writer = new FileWriter(jsonDirectory(directory))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(taskPresets, writer);
        }
    }

    public static ArrayList<TaskPreset> fromJson(String directory) throws IOException {
        try (FileReader reader = new FileReader(jsonDirectory(directory))) {
            return new Gson().fromJson(reader, new TypeToken<ArrayList<TaskPreset>>(){}.getType());
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
