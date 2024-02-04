package com.example.jdbcmysqlfull1;

import javafx.beans.property.SimpleObjectProperty;

public class Employee {
    private final SimpleObjectProperty<Object>[] properties;

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
}
