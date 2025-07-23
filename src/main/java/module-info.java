module com.registration {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    
    opens com.registration to javafx.fxml;
    opens com.registration.controllers to javafx.fxml;
    opens com.registration.models to javafx.fxml, javafx.base;
    
    exports com.registration;
    exports com.registration.controllers;
    exports com.registration.models;
    exports com.registration.utils;
}