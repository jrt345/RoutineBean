module com.example.routinebean {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.apache.commons.io;

    opens com.example.routinebean to javafx.fxml;
    exports com.example.routinebean;
    exports com.example.routinebean.utils;
    opens com.example.routinebean.controllers to javafx.fxml;
    exports com.example.routinebean.controllers;
    exports com.example.routinebean.properties;
    opens com.example.routinebean.data to com.google.gson;
    exports com.example.routinebean.data;
}