package implementations;

import config.JdbcConfig;
import entities.Expense;
import entities.ExpenseCategory;
import entities.ExpenseDto;
import interfaces.ExpenseDAO;
import implementations.ExpenseCategoryDaoImpl;
import interfaces.ExpenseCategoryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseDaoImpl implements ExpenseDAO {
    private final Connection connection = JdbcConfig.getConnection();
    private final ExpenseCategoryDAO expenseCategoryDao = new ExpenseCategoryDaoImpl();
    @Override
    public void insert(ExpenseDto expenseDto) {
        String sqlInsert = "INSERT INTO EXPENSES (amount, description, category_id, date) VALUES (?, ?, ?, ?)";

        try {
            // Crear un nuevo objeto Expense y establecer sus valores
            Expense newExpense = new Expense();
            newExpense.setAmount(expenseDto.getAmount());
            newExpense.setDescription(expenseDto.getDescription());
            newExpense.setCategory(expenseDto.getCategory());
            newExpense.setDate(expenseDto.getDate());

            // Obtener una conexión a la base de datos
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);

            // Establecer los parámetros en la consulta preparada
            preparedStatement.setDouble(1, newExpense.getAmount());
            preparedStatement.setString(2, newExpense.getDescription());
            ExpenseCategory expCatAux = newExpense.getCategory();
            preparedStatement.setInt(3, expCatAux.getId());
            preparedStatement.setString(4, newExpense.getDate());

            // Ejecutar la consulta para insertar el nuevo gasto
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("The expense was successfully loaded into the database.");
            } else {
                System.out.println("The expense couldn't be loaded successfully.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExpenseDto> getAll() {
        String sqlGetAll = "SELECT * FROM EXPENSES";
        List<ExpenseDto> expenseDtos = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetAll);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                int expenseCategoryId = resultSet.getInt("category_id");
                String date = resultSet.getString("date");
                int id = resultSet.getInt("id");

                ExpenseCategory newExpenseCategory = expenseCategoryDao.getById(expenseCategoryId);
                ExpenseDto newExpense = new ExpenseDto(amount,description ,newExpenseCategory, date);
                newExpense.setId(id);
                expenseDtos.add(newExpense);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expenseDtos;
    }

    @Override
    public Expense getById(int idExpense) {
        Expense expense = new Expense();
        String sqlById = "SELECT * FROM EXPENSES WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlById);
            preparedStatement.setInt(1, idExpense);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                int idCategory = resultSet.getInt("category_id");
                String date = resultSet.getString("date");

                ExpenseCategory categorySearch = expenseCategoryDao.getById(idCategory);
                expense.setAmount(amount);
                expense.setCategory(categorySearch);
                expense.setDescription(description);
                expense.setDate(date);

                System.out.println("The expense was found successfully.");
            } else {
                System.out.println("There is no match for that id.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<ExpenseDto> getByDescription(String descriptionSearch) {
        List<ExpenseDto> expenseDtos = new ArrayList<>();
        String sqlGetByName = "SELECT * FROM EXPENSES WHERE description LIKE ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetByName);
            preparedStatement.setString(1, descriptionSearch);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Se comprueba si hay resultados
            if (!resultSet.next()) {
                System.out.println("There´s no match for that name.");
                return expenseDtos; // Devuelve la lista vacía si no hay coincidencias
            }

            do {
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                int idCategory = resultSet.getInt("category_id");
                String date = resultSet.getString("date");
                int id = resultSet.getInt("id");

                ExpenseCategory categorySearch = expenseCategoryDao.getById(idCategory);

                ExpenseDto newExpense = new ExpenseDto(amount, description, categorySearch, date);
                newExpense.setId(id);
                expenseDtos.add(newExpense);
            } while (resultSet.next());

            System.out.println("There are matches for that name.");
            return expenseDtos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Expense expense) {
        String sqlUpdate = "UPDATE EXPENSES SET amount = ?, description = ?, category_id = ?, date = ? WHERE id = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            preparedStatement.setDouble(1, expense.getAmount());
            preparedStatement.setString(2, expense.getDescription());
            preparedStatement.setInt(3, expense.getCategory().getId());
            preparedStatement.setString(4, expense.getDate());
            preparedStatement.setInt(5, expense.getId());

            int updateComplete = preparedStatement.executeUpdate();

            if (updateComplete > 0) {
                System.out.println("The update was successfully loaded.");
            } else {
                System.out.println("The update couldn´t be completed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int idDelete) {
        String sqlDelte = "DELETE FROM EXPENSES WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDelte);
            preparedStatement.setInt(1, idDelete);

            int deleteComplete = preparedStatement.executeUpdate();

            if (deleteComplete > 0) {
                System.out.println("The expense was successfully deleted from the database.");
            } else {
                System.out.println("The expense couldn´t be deleted from the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
