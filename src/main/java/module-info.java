module com.example.routinebean {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example.routinebean to javafx.fxml;
    exports com.example.routinebean;
    exports com.example.routinebean.utils;
    exports com.example.routinebean.controllers;
    opens com.example.routinebean.controllers to javafx.fxml;
    exports com.example.routinebean.properties;
    opens com.example.routinebean.data to com.google.gson;
    exports com.example.routinebean.data;
}