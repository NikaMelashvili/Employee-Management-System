module com.example.jdbcmysqlfull1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.jdbcmysqlfull1 to javafx.fxml;
    exports com.example.jdbcmysqlfull1;
    exports com.example.jdbcmysqlfull1.database;
    opens com.example.jdbcmysqlfull1.database to javafx.fxml;
    exports com.example.jdbcmysqlfull1.controllers;
    opens com.example.jdbcmysqlfull1.controllers to javafx.fxml;
}