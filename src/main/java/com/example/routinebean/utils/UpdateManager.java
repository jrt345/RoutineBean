package com.example.routinebean.utils;

import com.example.routinebean.App;
import com.example.routinebean.controllers.NewUpdatePromptController;
import com.example.routinebean.utils.properties.AppProperties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager {

    public static final String CURRENT_VERSION = "1.0.0";

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

            String[] currentVersionsStringArray = CURRENT_VERSION.split("\\.");
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

    public static void showUpdateDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("newUpdatePrompt.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Update Available");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(AppUtils.ICON);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    public static void showUpdateDialog(AppProperties properties) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("newUpdatePrompt.fxml"));
        Parent root = fxmlLoader.load();

        NewUpdatePromptController controller = fxmlLoader.getController();
        controller.setProperties(properties);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Update Available");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(AppUtils.ICON);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }
}
