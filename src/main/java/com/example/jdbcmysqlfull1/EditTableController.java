package com.example.jdbcmysqlfull1;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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
import java.util.Map;
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
    Map<String, Integer> columnDataTypeAndIndex;
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
                ObservableList<String> columnNames = ReverseDataBase.getColumnNames(currentTableName);
                columnSelectorFx.setItems(columnNames);
                currentColumnName = columnSelectorFx.getValue();
                ReverseDataBase reverseDataBase = new ReverseDataBase();
                ObservableList<String> colProps = reverseDataBase.getColumnNames(currentTableName);
                ObservableList<String> colDataTypes = reverseDataBase.getColumnDataTypes(currentTableName);
                tableViewing(colDataTypes, colProps);
                columnSelectorFx.setOnAction(event1 -> {
                    try {
                        columnDataTypeAndIndex = ReverseDataBase.getColumnIndexAndType(currentColumnName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
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
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName));
        refreshData(dataTypes, columnProps);
    }
    //overloader-method 2. loads a selected column form a selected table
    public void tableViewing(String columnName, String columnDataType) throws SQLException {
        dataSheet.getColumns().clear();

        TableColumn<Employee, Object> column = new TableColumn<>(columnName);

        if ("INT".equalsIgnoreCase(columnDataType) || "DECIMAL".equalsIgnoreCase(columnDataType)) {
            column.setCellValueFactory(cellData -> cellData.getValue().getProperty(columnDataTypeAndIndex.get(columnDataType)));
        } else if ("VARCHAR".equalsIgnoreCase(columnDataType) || "DATE".equalsIgnoreCase(columnDataType)) {
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProperty(columnDataTypeAndIndex.get(columnDataType))));
        } else {
            System.out.println("Table viewing went wrong");
        }
        dataSheet.getColumns().add(column);
        emp.clear();
        emp.addAll(db.getAllEmployees(columnName, currentTableName));
        refreshData(columnDataType, columnName);
    }
    //handles a multi-column refresh
    public void refreshData(ObservableList<String>dataTypes, ObservableList<String> columnProps) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName));
        dataSheet.setItems(emp);
    }
    //handles a single-column refresh
    public void refreshData(String dataType, String columnName) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(dataType, columnName));
        dataSheet.setItems(emp);
    }
}
