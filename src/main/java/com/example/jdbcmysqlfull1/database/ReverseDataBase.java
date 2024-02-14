package com.example.jdbcmysqlfull1.database;

import com.example.jdbcmysqlfull1.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ReverseDataBase {
    static String dataBaseName = "javaclient";
    static String adminDataBaseName = "javaadmin";
    static String user;
    static String password;

    public ReverseDataBase(String user, String password) {
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
    public static ObservableList<String> getColumnNames(String tableName, String dataBase) throws SQLException {
        useDb(dataBase);
        ObservableList<String> columnNamesList = FXCollections.observableArrayList();
        Connection connection = getConnection(dataBase);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");
            columnNamesList.add(columnName);
        }
        System.out.println(columnNamesList);
        return columnNamesList;
    }

    public static ObservableList<String> getColumnDataTypes(String tableName, String dataBase, int id) throws SQLException {
        useDb(dataBase);
        ObservableList<String> columnTypesList = FXCollections.observableArrayList();
        Connection connection = getConnection(dataBase);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

        while (columnsResultSet.next()) {
            String columnType = columnsResultSet.getString("TYPE_NAME");
            columnTypesList.add(columnType);
        }
        System.out.println(columnTypesList);
        return columnTypesList;
    }
    public static String getColumnDataTypes(String columnName, String tableName) throws SQLException {
        useDb(dataBaseName);
        Connection connection = getConnection(dataBaseName);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, columnName);

        String columnType = null;
        if (columnsResultSet.next()) {
            columnType = columnsResultSet.getString("TYPE_NAME");
        }
        System.out.println(columnType);
        return columnType;
    }
}
