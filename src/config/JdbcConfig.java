package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConfig {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/expenseReport";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static void createTable() {

        try (Connection conn = getConnection()){

            String createCategoryExpenseTableSql = "CREATE TABLE IF NOT EXISTS EXPENSE_CATEGORY(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255));";
            try (var stmtCategoryExpense = conn.createStatement()) {
                stmtCategoryExpense.executeUpdate(createCategoryExpenseTableSql);
                System.out.println("Table CATEGORY_EXPENSES crated successfully");
            }

            String createExpenseTableSql = "CREATE TABLE IF NOT EXISTS EXPENSES(" + //Primary key, monto, descripcion, categoria y fecha
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "amount VARCHAR(20)," +
                    "description VARCHAR(255)," +
                    "category_id INT," + //FOREIGN KEY referencing CATEGORY_EXPENSES
                    "date VARCHAR(50)," +
                    "FOREIGN KEY (category_id) REFERENCES EXPENSE_CATEGORY (id));"; //defining foreing key contraint
            try (var stmtExpense = conn.createStatement()){
                stmtExpense.executeUpdate(createExpenseTableSql);
                System.out.println("Table EXPENSES created successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


//Tablas Expenses CategoryExpenses
