module com.example.routinebean {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.routinebean to javafx.fxml;
    exports com.example.routinebean;
    exports com.example.routinebean.utils;
    exports com.example.routinebean.controllers;
    opens com.example.routinebean.controllers to javafx.fxml;
    exports com.example.routinebean.properties;
    exports com.example.routinebean.data;
}