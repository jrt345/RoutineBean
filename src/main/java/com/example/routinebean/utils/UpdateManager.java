package com.example.routinebean.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager {

    private static final String stringCurrentVersion = "1.0.0";

    public static String getLatestVersion() {
        try {
            URL latestVersion = new URL("https://raw.githubusercontent.com/jrt345/RoutineBean/master/README.md");
            URLConnection gitHub = latestVersion.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(gitHub.getInputStream()));

            String stringCurrentVersion = in.readLine();
            in.close();

            return stringCurrentVersion.replaceAll("<", "")
                    .replaceAll("!", "")
                    .replaceAll("-", "")
                    .replaceAll("Version", "")
                    .replaceAll(">", "")
                    .replaceAll("\\s", "");
        } catch (IOException e) {
            return "0.0.0";
        }
    }
}
