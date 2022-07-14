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


    public static boolean isUpdateAvailable() {
        try {
            String stringLatestVersion = getLatestVersion();

            String[] currentVersionsStringArray = stringCurrentVersion.split("\\.");
            String[] latestVersionsStringArray = stringLatestVersion.split("\\.");

            int[] currentVersionsIntArray = new int[3];
            int[] latestVersionsIntArray = new int[3];

            for (int i = 0;i < 3;i++){
                currentVersionsIntArray[i] = Integer.parseInt(currentVersionsStringArray[i]);
                latestVersionsIntArray[i] = Integer.parseInt(latestVersionsStringArray[i]);
            }

            if (currentVersionsIntArray[0] < latestVersionsIntArray[0]){
                return true;
            } else if (currentVersionsIntArray[1] < latestVersionsIntArray[1]) {
                return true;
            } else {
                return currentVersionsIntArray[2] < latestVersionsIntArray[2];
            }
        } catch (Exception e) {
            return false;
        }
    }
}
