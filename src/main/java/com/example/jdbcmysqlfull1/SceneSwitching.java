package com.example.jdbcmysqlfull1;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitching {
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void switchSceneToCreateEdit(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("app-controller.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Employee management system");
        stage.setResizable(false);
        stage.show();
    }
    public void switchSceneToEditTable(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("edit-table.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edit management system");
        stage.setResizable(false);
        stage.show();
    }public void switchSceneToStartPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("start-page.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edit management system");
        stage.setResizable(false);
        stage.show();
    }
    public void switchScene(ActionEvent event, String fxml) throws IOException {
        root = FXMLLoader.load(getClass().getResource(fxml + ".fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edit management system");
        stage.setResizable(false);
        stage.show();
    }public void switchScene(ActionEvent event, String fxml, String title) throws IOException {
        root = FXMLLoader.load(getClass().getResource(fxml + ".fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edit management system");
        stage.setResizable(false);
        stage.show();
    }

}
