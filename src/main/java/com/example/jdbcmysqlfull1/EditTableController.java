package com.example.jdbcmysqlfull1;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class EditTableController implements Initializable {
    @FXML
    private ComboBox<String> tableSelector;
    @FXML
    private TableView<Employee> dataSheet;
    @FXML
    private AnchorPane layout;
    String tableName = "table_info";
    String currentTableName;
    ObservableList<Employee> emp = FXCollections.observableArrayList();
    String dataBase = "javaclient";
    String url = "jdbc:mysql://localhost:3306/" + dataBase;
    String user = "root";
    String password = "thegoatlevi123";
    Database db = new Database(url, user, password);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<String> tableName = Database.retrieveCreatedTableNames();
            tableSelector.setItems(tableName);
            tableSelector.setOnAction(e -> {
                onTableSelected(e);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void onTableSelected(ActionEvent event) {
        currentTableName = tableSelector.getValue();
        if (currentTableName != null) {
            try {
                dataSheet.getColumns().clear();
                dataSheet.getItems().clear();
                ReverseDataBase reverseDataBase = new ReverseDataBase();
                ObservableList<String> colProps = reverseDataBase.getColumnNames(currentTableName);
                ObservableList<String> colDataTypes = reverseDataBase.getColumnDataTypes(currentTableName);
                tableViewing(colDataTypes, colProps);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void tableViewing(ObservableList<String> dataTypes, ObservableList<String> columnProps) throws SQLException {
        dataSheet.getColumns().clear();

        for (int i = 0; i < columnProps.size(); i++) {
            String columnType = dataTypes.get(i);
            TableColumn<Employee, Object> column = new TableColumn<>(columnProps.get(i));

            if ("INT".equalsIgnoreCase(columnType) || "DECIMAL".equalsIgnoreCase(columnType)) {
                int finalI = i;
                column.setCellValueFactory(cellData -> cellData.getValue().getProperty(finalI));
            } else if ("VARCHAR".equalsIgnoreCase(columnType) || "DATE".equalsIgnoreCase(columnType)) {
                int finalI1 = i;
                column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProperty(finalI1).get()));
            } else {
                System.out.println("Table viewing went wrong");
            }
            dataSheet.getColumns().add(column);
        }
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName));

//        if (!layout.getChildren().contains(dataSheet)) {
//            layout.getChildren().add(dataSheet);
//        }
        refreshData(dataTypes, columnProps);
    }

    public void refreshData(ObservableList<String>dataTypes, ObservableList<String> columnProps) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName));
        dataSheet.setItems(emp);
    }
}
