package com.example.jdbcmysqlfull1.database;

import com.example.jdbcmysqlfull1.Employee;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Property<Object> {
    String url;
    static String user;
    static String password;
    static String dataBaseName = "javaclient";
    static String adminDataBaseName = "javaadmin";
    public static String userTableName;
    Map<String, String> columnData = new HashMap<>();
    public ObservableList<String> dataTypesSql = FXCollections.observableArrayList();
    public ObservableList<String> columnProperties = FXCollections.observableArrayList();
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
    public static void useDb(String dataBaseName) throws SQLException {
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
    public String[] mutateArray(String[] arr, String tableName, String placeHolder){
        if (arr == null || arr.length == 0) {
            return arr;
        }
        int index = 0;
        String element = "user" + placeHolder;
        System.out.println(element);
        for(int i = arr.length - 1; i > index; i--) {
            arr[i] = arr[i - 1];
        }
        arr[index] = element;
        return arr;
    }
    public String[] mutateArray(String[] arr, String placeHolder){
        if (arr == null || arr.length == 0) {
            return arr;
        }
        int index = 0;
        String element = placeHolder;
        System.out.println(element);
        for(int i = arr.length - 1; i > index; i--) {
            arr[i] = arr[i - 1];
        }
        arr[index] = element;

        for(int i = 0; i < arr.length; i++){
            System.out.println(arr[i]);
        }
        return arr;
    }

    public String[] listToArr(ObservableList<String> list){
        String[] newArr = new String[list.size() + 1];
        newArr = list.toArray(newArr);
        String placeHolder = "INT AUTO_INCREMENT PRIMARY KEY";
        newArr = mutateArray(newArr, placeHolder);

        for(int i = 0; i < newArr.length; i++){
            System.out.println(newArr[i]);
        }
        return newArr;
    }

    public void createTable(String tableName, String[] cols, ObservableList<String> dataTypesList) throws SQLException {
        userTableName = tableName;
        String placeHolder = "_id";
        cols = mutateArray(cols, tableName, placeHolder);
        String[] dataTypeArr = listToArr(dataTypesList);
        ObservableList<String> dataTypes = FXCollections.observableArrayList(dataTypeArr);
        Connection connection = null;
        try {
            connection = getConnection(dataBaseName);
            useDb(dataBaseName);
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
                    case "INT AUTO_INCREMENT PRIMARY KEY":
                        dataTypesSql.add("INT AUTO_INCREMENT PRIMARY KEY");
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
            useDb(adminDataBaseName);
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
            useDb(adminDataBaseName);
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
    public static ObservableList<String> retrieveCreatedTableNames(String columnName, String tableName, String dataBase) throws SQLException {
        Connection connection = null;
        ObservableList<String> tables = FXCollections.observableArrayList();

        try {
            connection = getConnection(dataBase);
            String sql = "SELECT " + columnName + " FROM " + tableName;
            System.out.println("SQL Query: " + sql);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String columnValue = resultSet.getString(columnName);
                    tables.add(columnValue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return tables;
    }

    public void addRow(List<StringProperty> textProperties, String[] cols) throws SQLException {
        Connection connection = getConnection(dataBaseName);
        useDb(dataBaseName);

        StringBuilder createQuery = new StringBuilder("INSERT INTO " + userTableName + " (");
        for (int i = 1; i < cols.length; i++) {
            createQuery.append(cols[i]);
            if (i < cols.length - 1) {
                createQuery.append(", ");
            }
        }
        createQuery.append(") VALUES (");
        for (int i = 1; i < cols.length; i++) {
            createQuery.append("?");
            if (i < cols.length - 1) {
                createQuery.append(", ");
            }
        }
        createQuery.append(")");

        System.out.println("Generated Query: " + createQuery.toString());
        String fullQuery = createQuery.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(fullQuery)) {
            int parameterIndex = 1;
            for (int i = 0; i < textProperties.size(); i++) {
                String value = textProperties.get(i).get();
                if (value == null || value.isEmpty()) {
                    preparedStatement.setNull(parameterIndex, Types.VARCHAR);
                } else {
                    preparedStatement.setString(parameterIndex, value);
                }
                parameterIndex++;
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
    //    returns full table
    public ObservableList<Employee> getAllEmployees(ObservableList<String> dataTypesSql, ObservableList<String> columnProperties, String userTableName, String dataBase) throws SQLException {
        System.out.println(dataBase + " this is the user db");
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();
        Connection connection = null;
        try {
            connection = getConnection(dataBase);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + userTableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = new Object[dataTypesSql.size()];

                for (int i = 0; i < dataTypesSql.size(); i++) {
                    String columnType = dataTypesSql.get(i);

                    if ("INT".equalsIgnoreCase(columnType) || "INT AUTO_INCREMENT PRIMARY KEY".equalsIgnoreCase(columnType)) {
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
//        for (int i = 0; i < employeeList.size(); i++) {
//            System.out.println("emp list all " + employeeList.get(i));
//        }
        return employeeList;
    }
    //    returns column
    public static ObservableList<Employee> getAllEmployees(String columnName, String userTableName) throws SQLException {
        String columnDataType = ReverseDataBase.getColumnDataTypes(columnName, userTableName);

        Connection connection = null;
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        try {
            useDb(dataBaseName);
            connection = getConnection(dataBaseName);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + columnName + " FROM " + userTableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = new Object[1];

                if("INT AUTO_INCREMENT PRIMARY KEY".equalsIgnoreCase(columnDataType)){
                    rowData[0] = resultSet.getInt(columnName);
                }else if ("INT".equalsIgnoreCase(columnDataType)) {
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
