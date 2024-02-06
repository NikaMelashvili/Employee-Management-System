package com.example.jdbcmysqlfull1;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CreateEditController {
    String dataBase = "javaclient";
    String url = "jdbc:mysql://localhost:3307/" + dataBase;
    String user = "root";
    String password = "thegoatlevi123";
    @FXML
    private TextField createTableField;
    @FXML
    private TextField columnsAmount;
    @FXML
    private AnchorPane layout;
    String[] cols;
    int columnAmount;
    String newTableNameForQuery;
    ObservableList<String> selectedValues = FXCollections.observableArrayList();
    ObservableList<StringProperty> rows = FXCollections.observableArrayList();
    ObservableList<Employee> emp = FXCollections.observableArrayList();
    private TableView<Employee> dataSheet = new TableView<>();
    Database db = new Database(url,user,password);
    double initialY = 50.0;
    public void nextTableBtnAction() {
        newTableNameForQuery = createTableField.getText();
        columnAmount = Integer.parseInt(columnsAmount.getText());
        cols = new String[columnAmount];
        layout.getChildren().clear();
        dynamicFiledElements(cols, columnAmount, newTableNameForQuery);
    }
    public void dynamicFiledElements(String[] cols, int columnAmount, String newTableNameForQuery){
        AtomicReference<Double> n = new AtomicReference<>(0.0);
        double totalHeight = 0.0;
        for (int i = 0; i < columnAmount; i++) {
            Label colNum = new Label("Enter column N" + (i + 1) + " name");
            TextField columnNum = new TextField();
            columnNum.getStyleClass().add(".borderStyles");
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getStyleClass().add("custom-combo-box");
            comboBox.getItems().addAll(
                    "INT",
                    "VARCHAR",
                    "DATE",
                    "DECIMAL"
            );
            AnchorPane.setLeftAnchor(colNum, 10.0);
            AnchorPane.setTopAnchor(colNum, initialY + n.get());
            AnchorPane.setLeftAnchor(columnNum, 10.0);
            AnchorPane.setTopAnchor(columnNum, initialY + 30.0 + n.get());
            AnchorPane.setTopAnchor(comboBox, initialY + 30.0 + n.get());
            AnchorPane.setLeftAnchor(comboBox, 200.0);

            comboBox.setLayoutX(colNum.getLayoutX() + colNum.prefWidth(0));
            n.updateAndGet(v -> v + 60.0);
            totalHeight = initialY + n.get();

            layout.getChildren().addAll(colNum, columnNum, comboBox);
            int finalI = i;
            columnNum.textProperty().addListener((observable, oldValue, newValue) -> {
                cols[finalI] = newValue;
            });
            ComboBox<String> finalComboBox = comboBox;
            comboBox.setOnAction(e -> {
                String selectedItem = finalComboBox.getValue();
                if (selectedItem != null) {
                    selectedValues.add(selectedItem);
                }
            });
        }
        Button createTableBtn = new Button("Create");
        createTableBtn.getStyleClass().add("nextButton");
        createTableBtn.setLayoutX(50.0);
        AnchorPane.setTopAnchor(createTableBtn, totalHeight + 20.0);
        layout.getChildren().add(createTableBtn);

        n.set(0.0);

        double finalTotalHeight = totalHeight;
        createTableBtn.setOnAction(e -> {
            try {
                db.createTable(newTableNameForQuery, cols, selectedValues);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            layout.getChildren().clear();

            // insert rows section
            List<TextField> rowNumTextFields = new ArrayList<>();
            for (int i = 0; i < columnAmount; i++) {
                Label rowNum = new Label("Enter row element N" + (i + 1) + " value");
                TextField rowNumText = new TextField();
                rowNumText.setId("rowNumberText");

                AnchorPane.setLeftAnchor(rowNum, 10.0);
                AnchorPane.setTopAnchor(rowNum, initialY + n.get());
                AnchorPane.setLeftAnchor(rowNumText, 10.0);
                AnchorPane.setTopAnchor(rowNumText, initialY + 30.0 + n.get());

                n.updateAndGet(v -> v + 60.0);
                layout.getChildren().addAll(rowNum, rowNumText);
                rows.add(rowNumText.textProperty());
                rowNumTextFields.add(rowNumText);
                System.out.println(rows.get(i));
            }
            Button addRowBtn = new Button("Add row");
            addRowBtn.getStyleClass().add("nextButton");
            addRowBtn.setLayoutX(50.0);
            AnchorPane.setTopAnchor(addRowBtn, finalTotalHeight + 20.0);
            layout.getChildren().add(addRowBtn);

            addRowBtn.setOnAction(event1 -> {
                try {
                    db.addRow(rows);
                    tableViewing(db.dataTypesSql, db.columnProperties);
                    for (TextField textField : rowNumTextFields) {
                        textField.clear();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
    }
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
        emp.addAll(db.getAllEmployees(db.dataTypesSql, db.columnProperties, db.userTableName));
        AnchorPane.setRightAnchor(dataSheet, 10.0);

        if (!layout.getChildren().contains(dataSheet)) {
            layout.getChildren().add(dataSheet);
        }
        refreshData();
    }

    public void refreshData() throws SQLException {
        emp.clear();
        emp.addAll(db.getAllEmployees(db.dataTypesSql, db.columnProperties, db.userTableName));
        dataSheet.setItems(emp);
    }
}
