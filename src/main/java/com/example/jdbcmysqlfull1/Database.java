package com.example.jdbcmysqlfull1;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Property<Object> {
    String url;
    String user;
    String password;
    String dataBaseName;
    String userTableName;
    Map<String, String> columnData = new HashMap<>();
    ObservableList<String> dataTypesSql = FXCollections.observableArrayList();
    ObservableList<String> columnProperties = FXCollections.observableArrayList();
    public Database(String url, String user, String password, String dataBaseName){
        this.url = url;
        this.user = user;
        this.password = password;
        this.dataBaseName = dataBaseName;
    }
    public Connection getConnection() throws SQLException {
        String dbUrl = url;
        String username = user;
        String pass = password;
        return DriverManager.getConnection(dbUrl, username, pass);
    }
    public void createTable(String tableName, String[] cols, ObservableList<String> dataTypes) {
        userTableName = tableName;
        Connection connection = null;
        try {
            connection = getConnection();
            String useDb = "USE " + dataBaseName;
            try (PreparedStatement usePrepStatement = connection.prepareStatement(useDb)) {
                usePrepStatement.execute();
            }
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
    public void addRow(List<StringProperty> textProperties) throws SQLException {
        Connection connection = getConnection();
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

    public ObservableList<Employee> getAllEmployees() throws SQLException {
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();
        Connection connection = getConnection();
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
