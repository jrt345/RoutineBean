module com.example.routinebuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.routinebuilder to javafx.fxml;
    exports com.example.routinebuilder;
    exports com.example.routinebuilder.controllers;
    opens com.example.routinebuilder.controllers to javafx.fxml;
}