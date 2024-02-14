package com.example.jdbcmysqlfull1.controllers;

import com.example.jdbcmysqlfull1.Employee;
import com.example.jdbcmysqlfull1.database.Database;
import com.example.jdbcmysqlfull1.database.ReverseDataBase;
import javafx.beans.property.ObjectProperty;
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
    private ComboBox<String> columnSelectorFx;
    @FXML
    private TableView<Employee> dataSheet;
    @FXML
    private AnchorPane layout;
    String currentTableName;
    String currentColumnName;
    String colDataType;
    ObservableList<Employee> emp = FXCollections.observableArrayList();
    ObservableList<String> currentDataTypes = FXCollections.observableArrayList();
    ObservableList<String> currentColProps = FXCollections.observableArrayList();
    String currentColumnDataType;
    String dataBase = "javaclient";
    String url = "jdbc:mysql://localhost:3306/" + dataBase;
    String user = "root";
    String password = "thegoatlevi123";
    Database db = new Database(url, user, password);
    ReverseDataBase reverseDataBase = new ReverseDataBase(user, password);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<String> tableName = Database.retrieveCreatedTableNames();
            tableSelector.setItems(tableName);
            tableSelector.setOnAction(e -> {
                try {
                    onTableSelected(e);
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
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
                ObservableList<String> columnNames = ReverseDataBase.getColumnNames(currentTableName, "javaclient");
                columnSelectorFx.setItems(columnNames);
                ReverseDataBase reverseDataBase = new ReverseDataBase("root", "thegoatlevi123");
                ObservableList<String> colProps = reverseDataBase.getColumnNames(currentTableName, "javaclient");
                ObservableList<String> colDataTypes = reverseDataBase.getColumnDataTypes(currentTableName, "javaclient", 1);
                tableViewing(colDataTypes, colProps);
                columnSelectorFx.setOnAction(event1 -> {
                    currentColumnName = columnSelectorFx.getValue();
                    try {
                        currentColumnDataType = ReverseDataBase.getColumnDataTypes(currentColumnName, currentTableName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        colDataType = ReverseDataBase.getColumnDataTypes(currentColumnName, currentTableName);
                        tableViewing(currentColumnName, colDataType);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    //overloader-method 1. loads a full table
    public void tableViewing(ObservableList<String> dataTypes, ObservableList<String> columnProps) throws SQLException {
        dataSheet.getColumns().clear();

        for (int i = 0; i < columnProps.size(); i++) {
            String columnType = dataTypes.get(i);
            TableColumn<Employee, Object> column = new TableColumn<>(columnProps.get(i));
            currentColProps.add(columnProps.get(i));
            currentDataTypes.add(columnType);

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
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, dataBase));
        refreshData(dataTypes, columnProps);
    }

    //overloader-method 2. loads a selected column form a selected table
    public void tableViewing(String columnName, String columnDataType) throws SQLException {
        dataSheet.getColumns().clear();
        ObservableList<String> colName = FXCollections.observableArrayList();
        ObservableList<String> dataName = FXCollections.observableArrayList();
        colName.add(columnName);
        dataName.add(columnDataType);
        for(int i = 0; i < colName.size(); i++){
            TableColumn<Employee, Object> column = new TableColumn<>(columnName);
            if ("INT".equalsIgnoreCase(columnDataType) || "DECIMAL".equalsIgnoreCase(columnDataType)) {
                int finalI = i;
                column.setCellValueFactory(cellData -> {
                    ObjectProperty<Object> property = cellData.getValue().getProperty(finalI);
                    return property != null ? new SimpleObjectProperty<>(property.getValue()) : new SimpleObjectProperty<>(null);
                });
            } else if ("VARCHAR".equalsIgnoreCase(columnDataType) || "DATE".equalsIgnoreCase(columnDataType)) {
                int finalI1 = i;
                column.setCellValueFactory(cellData -> {
                    ObjectProperty<Object> property = cellData.getValue().getProperty(finalI1);
                    return property != null ? new SimpleObjectProperty<>(property.getValue()) : new SimpleObjectProperty<>(null);
                });
            } else {
                System.out.println("Table viewing went wrong");
            }
            dataSheet.getColumns().add(column);
        }
        emp.clear();
        emp.addAll(db.getAllEmployees(columnName, currentTableName));
        refreshData(columnName);
    }
    //handles a multi-column refresh
    public void refreshData(ObservableList<String>dataTypes, ObservableList<String> columnProps) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, dataBase));
        dataSheet.setItems(emp);
    }
    //handles a single-column refresh
    public void refreshData(String columnName) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(columnName, currentTableName));
        dataSheet.setItems(emp);
    }
}
