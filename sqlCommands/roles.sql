CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50)
);

INSERT INTO roles (role_name)
VALUES 	("admin"),
		("employee"),
        ("user");

SELECT * FROM roles;