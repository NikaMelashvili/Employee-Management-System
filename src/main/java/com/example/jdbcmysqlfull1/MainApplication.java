package com.example.jdbcmysqlfull1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("start-page.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Employee management system");
        stage.setResizable(false);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}