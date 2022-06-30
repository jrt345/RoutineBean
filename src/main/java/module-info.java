module com.example.routinebuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.routinebean to javafx.fxml;
    exports com.example.routinebean;
    exports com.example.routinebean.utils;
    exports com.example.routinebean.controllers;
    opens com.example.routinebean.controllers to javafx.fxml;
    exports com.example.routinebean.utils.properties;
}