package com.example.jdbcmysqlfull1;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Property<Object> {
    String url;
    static String user;
    static String password;
    static String dataBaseName = "javaclient";
    static String adminDataBaseName = "javaadmin";
    static String userTableName;
    Map<String, String> columnData = new HashMap<>();
    ObservableList<String> dataTypesSql = FXCollections.observableArrayList();
    ObservableList<String> columnProperties = FXCollections.observableArrayList();
    public Database(){}
    public Database(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public static Connection getConnection(String dataBase) throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/" + dataBase;
        String username = user;
        String pass = password;
//        System.out.println("user: " + username + ", " + "url: " + dbUrl + ", " + "password: " + pass);
        return DriverManager.getConnection(dbUrl, username, pass);
    }

    public static void useAdminDataBase() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection(adminDataBaseName);
            String sql = "USE " + adminDataBaseName + ";";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    public static void useDataBase() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection(dataBaseName);
            String sql = "USE " + dataBaseName + ";";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    public void createTable(String tableName, String[] cols, ObservableList<String> dataTypes) throws SQLException {
        userTableName = tableName;
        Connection connection = null;
        try {
            connection = getConnection(dataBaseName);
            useDataBase();
            StringBuilder createQuery = new StringBuilder("CREATE TABLE " + tableName + " (");

            for (int i = 0; i < cols.length; i++) {
                columnProperties.add(cols[i]);
                createQuery.append(cols[i] + " ");
                switch (dataTypes.get(i)) {
                    case "INT":
                        dataTypesSql.add("INT");
                        columnData.put(cols[i], dataTypes.get(i));
                        createQuery.append(dataTypes.get(i));
                        break;
                    case "DATE":
                        dataTypesSql.add("DATE");
                        columnData.put(cols[i], dataTypes.get(i));
                        createQuery.append(dataTypes.get(i));
                        break;
                    case "VARCHAR":
                        dataTypesSql.add("VARCHAR");
                        columnData.put(cols[i], dataTypes.get(i));
                        createQuery.append(dataTypes.get(i) + "(50)");
                        break;
                    case "DECIMAL":
                        dataTypesSql.add("DECIMAL");
                        columnData.put(cols[i], dataTypes.get(i));
                        createQuery.append(dataTypes.get(i) + "(7, 3)");
                        break;
                    default:
                        System.out.println("Invalid data type " + dataTypes.get(i));
                        return;
                }
                if (i < cols.length - 1) {
                    createQuery.append(", ");
                }
            }
            createQuery.append(");");
            System.out.println("Generated Query: " + createQuery.toString());
            try (PreparedStatement preparedStatement = connection.prepareStatement(createQuery.toString())) {
                preparedStatement.executeUpdate();
                System.out.println("Table created successfully");
                insertTableNameToAdminDb(userTableName);
            } catch (SQLException e) {
                System.err.println("Error executing the query: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error closing the connection: " + ex.getMessage());
            }
        }
    }
    public void insertTableNameToAdminDb(String tableName) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection(adminDataBaseName);
            useAdminDataBase();
            String sql = "INSERT INTO table_info(table_name_) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tableName);
            preparedStatement.execute();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    public static ObservableList<String> retrieveCreatedTableNames() throws SQLException {
        Connection connection = null;
        ObservableList<String> tables = FXCollections.observableArrayList();
        String columnName = "table_name_";

        try {
            useAdminDataBase();
            connection = getConnection(adminDataBaseName);
            String sql = "SELECT " + columnName + " FROM " + adminDataBaseName + ".table_info";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String columnValue = resultSet.getString(columnName);
                    tables.add(columnValue);
                }
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return tables;
    }
    public void addRow(List<StringProperty> textProperties) throws SQLException {
        Connection connection = getConnection(dataBaseName);
        useDataBase();
        StringBuilder createQuery = new StringBuilder("INSERT INTO " + userTableName + " (");

        for (int i = 0; i < columnProperties.size(); i++) {
            createQuery.append(columnProperties.get(i));

            if (i < columnProperties.size() - 1) {
                createQuery.append(", ");
            }
        }
        createQuery.append(") VALUES (");

        for (int i = 0; i < columnProperties.size(); i++) {
            createQuery.append("?");

            if (i < columnProperties.size() - 1) {
                createQuery.append(", ");
            }
        }
        createQuery.append(")");

        System.out.println("Generated Query: " + createQuery.toString());
        String fullQuery = createQuery.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(fullQuery)) {
            for (int i = 0; i < dataTypesSql.size(); i++) {
                String columnType = dataTypesSql.get(i);

                if ("INT".equals(columnType)) {
                    String value = textProperties.get(i).get();
                    if (value == null || value.isEmpty()) {
                        preparedStatement.setNull(i + 1, Types.INTEGER);
                    } else {
                        int intValue = Integer.parseInt(value);
                        preparedStatement.setInt(i + 1, intValue);
                    }
                } else if (("VARCHAR".equals(columnType)) || ("DATE".equals(columnType))) {
                    preparedStatement.setString(i + 1, textProperties.get(i).get());
                } else if ("DECIMAL".equals(columnType)) {
                    String value = textProperties.get(i).get();
                    if (value == null || value.isEmpty()) {
                        preparedStatement.setNull(i + 1, Types.DECIMAL);
                    } else {
                        double doubleValue = Double.parseDouble(value);
                        preparedStatement.setDouble(i + 1, doubleValue);
                    }
                }
            }
            preparedStatement.executeUpdate();
            System.out.println("Row has been inserted successfully");
        } catch (SQLException e) {
            System.err.println("Error executing the query: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public ObservableList<Employee> getAllEmployees(ObservableList<String> dataTypesSql, ObservableList<String> columnProperties, String userTableName) throws SQLException {
        Connection connection = null;
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        try {
            useDataBase();
            connection = getConnection(dataBaseName);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + userTableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = new Object[dataTypesSql.size()];

                for (int i = 0; i < dataTypesSql.size(); i++) {
                    String columnType = dataTypesSql.get(i);

                    if ("INT".equalsIgnoreCase(columnType)) {
                        rowData[i] = resultSet.getInt(columnProperties.get(i));
                    } else if ("VARCHAR".equalsIgnoreCase(columnType) || "DATE".equalsIgnoreCase(columnType)) {
                        rowData[i] = resultSet.getString(columnProperties.get(i));
                    } else {
                        rowData[i] = resultSet.getDouble(columnProperties.get(i));
                    }
                }
                employeeList.add(new Employee(rowData));
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        for(int i = 0; i < employeeList.size(); i++){
            System.out.println("emp list all" + employeeList.get(i));
        }
        return employeeList;
    }
    public ObservableList<Employee> getAllEmployees(String columnName, String userTableName) throws SQLException {
        String columnDataType = ReverseDataBase.getColumnDataTypes(columnName, userTableName);

        Connection connection = null;
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        try {
            useDataBase();
            connection = getConnection(dataBaseName);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + columnName + " FROM " + userTableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = new Object[1];

                if ("INT".equalsIgnoreCase(columnDataType)) {
                    rowData[0] = resultSet.getInt(columnName);
                } else if ("VARCHAR".equalsIgnoreCase(columnDataType) || "DATE".equalsIgnoreCase(columnDataType)) {
                    rowData[0] = resultSet.getString(columnName);
                } else if ("DECIMAL".equalsIgnoreCase(columnDataType)) {
                    rowData[0] = resultSet.getDouble(columnName);
                } else {
                    System.err.println("Unsupported data type: " + columnDataType);
                }

                employeeList.add(new Employee(rowData));
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        for(int i = 0; i < employeeList.size(); i++){
            System.out.println("emp list " + employeeList.get(i));
        }
        return employeeList;
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void addListener(ChangeListener<? super Object> changeListener) {

    }

    @Override
    public void removeListener(ChangeListener<? super Object> changeListener) {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object o) {

    }

    @Override
    public void bind(ObservableValue<?> observableValue) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public void bindBidirectional(Property<Object> property) {

    }

    @Override
    public void unbindBidirectional(Property<Object> property) {

    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
