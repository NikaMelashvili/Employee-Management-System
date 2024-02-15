package com.example.jdbcmysqlfull1.controllers;

import com.example.jdbcmysqlfull1.Employee;
import com.example.jdbcmysqlfull1.database.Database;
import com.example.jdbcmysqlfull1.database.ReverseDataBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RolesController implements Initializable {
    @FXML
    private TableView<Employee> rolesTable;
    @FXML
    private TableView<Employee> usersTable;
    @FXML
    private ComboBox<String> userTableComboBox;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private ComboBox<String> userIdComboBox;
    @FXML
    private Button addBtn;
    ObservableList<Employee> emp = FXCollections.observableArrayList();
    ObservableList<Employee> emp1 = FXCollections.observableArrayList();
    ObservableList<String> currentColProps = FXCollections.observableArrayList();
    ObservableList<String> currentDataTypes = FXCollections.observableArrayList();
    String currentTableName;
    String currentIdNumberUser;
    Boolean notEmptyNum = false;
    String currentIdNumberRole;
    Boolean notEmptyRole = false;
    String currentColName = "user_id";
    String dataBase = "javaclient";
    String adminDataBase = "javaadmin";
    String user = "root";
    String url = "jdbc:mysql://localhost:3306/";
    String password = "thegoatlevi123";
    ReverseDataBase reverseDataBase = new ReverseDataBase(user, password);
    Database db = new Database(url, user, password);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            load();
            roleTable();
            ObservableList<String> items = loadARoleId();
            userIdComboBox.setItems(items);
            System.out.println(currentTableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() throws SQLException {
        ObservableList<String> dataTypes = ReverseDataBase.getColumnDataTypes("roles", adminDataBase, 1); // works
        System.out.println(dataTypes);
        ObservableList<String> colProps = ReverseDataBase.getColumnNames("roles", adminDataBase); // works
        System.out.println(colProps);
        tableViewing(dataTypes, colProps, "roles");
    }
    public void roleTable() throws SQLException {
        ObservableList<String> tableNames = Database.retrieveCreatedTableNames();
//        System.out.println(tableNames);
        userTableComboBox.setItems(tableNames);
        userTableComboBox.setOnAction(event -> {
            String selectedTable = userTableComboBox.getValue();
            currentTableName = selectedTable;
            System.out.println(currentTableName + " current table name");
            try {
                userBox();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                ObservableList<String> colNames = ReverseDataBase.getColumnNames(selectedTable, dataBase);
                ObservableList<String> colDataTypes = ReverseDataBase.getColumnDataTypes(selectedTable, dataBase, 1);
                tableViewingForUsers(colDataTypes, colNames, selectedTable);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public ObservableList<String> loadARoleId() throws SQLException {
        ObservableList<String> items;
        String roles = "roles";
        String colName = "role_id";
        items = Database.retrieveCreatedTableNames(colName, roles, adminDataBase);
        return items;
    }
    public void userBox() throws SQLException {
        System.out.println(currentTableName);
        ObservableList<String> userNamesList = Database.retrieveCreatedTableNames("user_id", currentTableName, dataBase);
        userComboBox.setItems(userNamesList);
        userComboBox.setOnAction(event -> {
            currentIdNumberUser = userComboBox.getValue();
            notEmptyNum = true;
        });
        userIdComboBox.setOnAction(event -> {
            currentIdNumberRole = userComboBox.getValue();
            notEmptyRole = true;
        });

            addBtn.setOnAction(event -> {
                try {
                    if(notEmptyNum && notEmptyRole == true) {
                        Database.addRow(currentIdNumberUser, currentTableName, currentIdNumberRole, "user_roles");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }
    public void tableViewing(ObservableList<String> dataTypes, ObservableList<String> columnProps, String currentTableName) throws SQLException {
        rolesTable.getColumns().clear();

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
            rolesTable.getColumns().add(column);
        }
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, adminDataBase));
        System.out.println(dataTypes + " " + columnProps + " " + currentTableName + " " + adminDataBase);
        refreshData(dataTypes, columnProps,currentTableName);
    }
    public void tableViewingForUsers(ObservableList<String> dataTypes, ObservableList<String> columnProps, String currentTableName) throws SQLException {
        usersTable.getColumns().clear();

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
            usersTable.getColumns().add(column);
        }
        emp1.clear();
        emp1.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, dataBase));
//        System.out.println(dataBase);
        refreshDataForUser(dataTypes, columnProps,currentTableName);
        System.out.println("refreshed data");
    }
    public void refreshData(ObservableList<String>dataTypes, ObservableList<String> columnProps, String currentTableName) throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, adminDataBase));
        System.out.println(dataTypes + " " + columnProps + " " + currentTableName + " " + adminDataBase);
        rolesTable.setItems(emp);
    }
    public void refreshDataForUser(ObservableList<String>dataTypes, ObservableList<String> columnProps, String currentTableName) throws SQLException {
        emp1.clear();
        emp1.addAll(db.getAllEmployees(dataTypes, columnProps, currentTableName, dataBase));
        usersTable.setItems(emp1);
    }
}
