# Summary: JavaFX MySQL Database Employee Management Application

This Java application, built with JavaFX, facilitates user interaction with a MySQL database. It allows users to input a table name and define the number of columns. For each column, users specify a name and select a data type from options like INT, VARCHAR, DATE, or DECIMAL. The "Create" button generates a corresponding SQL query and creates the table in the connected MySQL database.
Once a table is created, users can input values for each column to add rows. Clicking "Add row" executes a SQL query, inserting the provided data into the previously created table. The application dynamically adjusts its interface based on the specified number of columns, providing flexibility for table and row management. Additionally, it incorporates error handling for potential SQL exceptions during table creation or row insertion, offering relevant feedback to the user.
