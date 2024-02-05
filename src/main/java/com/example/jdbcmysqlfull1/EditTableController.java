package com.example.jdbcmysqlfull1;

import com.example.jdbcmysqlfull1.Database;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditTableController implements Initializable {
    @FXML
    private ComboBox<String> tableSelector;
    @FXML
    private TableView dataSheet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Database database = new Database();
            ObservableList<String> tableName = Database.retrieveCreatedTableNames("table_info");
            tableSelector.setItems(tableName);
            tableSelector.setOnAction(e -> {
                String selectedItem = tableSelector.getValue();
                if (selectedItem != null) {
                    try {
                        dataSheet = database.tableViewingForEdit(selectedItem);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.out.println("Not Null");
                } else {
                    System.out.println("Null");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void refreshData() throws SQLException {
        dataSheet.getItems().clear();
    }
}
