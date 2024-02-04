package com.example.jdbcmysqlfull1;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Employee {
    private final StringProperty stringValue;
    private final IntegerProperty intValue;
    private final DoubleProperty doubleValue;
    private ObservableList<Object> resultSets;
    public Employee(ObservableList<Object> recevedSets){
        for(int i = 0; i < recevedSets.size(); i++){
            this.resultSets.add(recevedSets.get(i));
        }
    }
    public Employee(String stringValue) {
        this.stringValue = new SimpleStringProperty(stringValue);
        this.intValue = new SimpleIntegerProperty(0);
        this.doubleValue = new SimpleDoubleProperty(0.0);
    }

    public Employee(int intValue) {
        this.stringValue = new SimpleStringProperty("");
        this.intValue = new SimpleIntegerProperty(intValue);
        this.doubleValue = new SimpleDoubleProperty(0.0);
    }

    public Employee(double doubleValue) {
        this.stringValue = new SimpleStringProperty("");
        this.intValue = new SimpleIntegerProperty(0);
        this.doubleValue = new SimpleDoubleProperty(doubleValue);
    }
    public StringProperty stringValueProperty() {
        return stringValue;
    }

    public IntegerProperty intValueProperty() {
        return intValue;
    }

    public DoubleProperty doubleValueProperty() {
        return doubleValue;
    }
    public String getStringValue() {
        return stringValue.get();
    }

    public int getIntValue() {
        return intValue.get();
    }

    public double getDoubleValue() {
        return doubleValue.get();
    }

}
