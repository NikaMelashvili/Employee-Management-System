package com.example.jdbcmysqlfull1;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;

public class Employee {
    private final SimpleObjectProperty<Object>[] properties;
    private TableColumn<Employee, Object> columnName;
    public Employee(Object... values) {
        int size = values.length;
        properties = new SimpleObjectProperty[size];

        for (int i = 0; i < size; i++) {
            properties[i] = new SimpleObjectProperty<>(values[i]);
        }
    }
    public SimpleObjectProperty<Object> getProperty(int index) {
        return properties[index];
    }
    public void setColumnName(TableColumn<Employee, Object> columnName){
        this.columnName = columnName;
    }
    public TableColumn<Employee, Object> getColumnName(int index) {
        return columnName;
    }
}
