package com.example.jdbcmysqlfull1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReverseDataBase {
    public ObservableList<String> getColumnNames(String tableName) throws SQLException, SQLException {
        Database.useDataBase();
        ObservableList<String> columnNamesList = FXCollections.observableArrayList();
        Connection connection = Database.getConnection(Database.dataBaseName);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");
            columnNamesList.add(columnName);
        }
        System.out.println(columnNamesList);
        return columnNamesList;
    }

    public ObservableList<String> getColumnDataTypes(String tableName) throws SQLException {
        Database.useDataBase();
        ObservableList<String> columnTypesList = FXCollections.observableArrayList();
        Connection connection = Database.getConnection(Database.dataBaseName);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

        while (columnsResultSet.next()) {
            String columnType = columnsResultSet.getString("TYPE_NAME");
            columnTypesList.add(columnType);
        }
        System.out.println(columnTypesList);
        return columnTypesList;
    }
}
