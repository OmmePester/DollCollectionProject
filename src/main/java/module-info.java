module com.example.dollcollectionproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.dollcollectionproject to javafx.fxml;
    exports com.example.dollcollectionproject;
    exports com.example.dollcollectionproject.model;
    opens com.example.dollcollectionproject.model to javafx.fxml;
}