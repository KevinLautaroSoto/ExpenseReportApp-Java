package implementations;

import config.JdbcConfig;
import entities.ExpenseCategory;
import entities.ExpenseCategoryDto;
import interfaces.ExpenseCategoryDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryDaoImpl implements ExpenseCategoryDAO {
    private final Connection connection = JdbcConfig.getConnection();

    @Override
    public void insert(ExpenseCategoryDto expenseCategoryDto) {
        String sqlInsert = "INSERT INTO EXPENSE_CATEGORY (name) VALUES (?)";

        try {
            ExpenseCategory newExpenseCat = new ExpenseCategory();
            newExpenseCat.setName(expenseCategoryDto.getName());

            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
            preparedStatement.setString(1, newExpenseCat.getName());

            int insertionComplete = preparedStatement.executeUpdate();

            if(insertionComplete > 0) {
                System.out.println("The expense category was successfully loaded into the database.");
            } else {
                System.out.println("The expense category could´t be loaded into the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExpenseCategoryDto> getAll() {
        List<ExpenseCategoryDto> expenses = new ArrayList<>();
        String sqlGetAll = "SELECT * FROM EXPENSE_CATEGORY";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetAll);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");

                ExpenseCategoryDto  newExpense = new ExpenseCategoryDto(id, name);
                expenses.add(newExpense);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expenses;
    }

    @Override
    public ExpenseCategory getById(int idExpCategory) {
        ExpenseCategory expenseCategory = new ExpenseCategory();
        String sqlGetById = "SELECT * FROM EXPENSE_CATEGORY WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetById);
            preparedStatement.setInt(1, idExpCategory);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                expenseCategory.setName(name);
                expenseCategory.setId(id);

                System.out.println("There is a match for that id.");
                return expenseCategory;
            } else {
                System.out.println("There is no match for that id.");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExpenseCategoryDto> getByName(String nameCategory) {
        List<ExpenseCategoryDto> expenseCategoryDtos = new ArrayList<>();
        String sqlGetByName = "SELECT * FROM EXPENSE_CATEGORY WHERE name LIKE ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetByName);
            preparedStatement.setString(1, nameCategory);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name"); // <-- Corregir aquí

                ExpenseCategoryDto newExpenseCategoryDto = new ExpenseCategoryDto(id, name);
                expenseCategoryDtos.add(newExpenseCategoryDto);
            }

            if (!expenseCategoryDtos.isEmpty()) {
                System.out.println("There are matches for that name.");
            } else {
                System.out.println("There's no match for that name.");
            }

            return expenseCategoryDtos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExpenseCategory expenseCategory) {
        String sqlUpdate = "UPDATE EXPENSE_CATEGORY SET name = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            preparedStatement.setString(1, expenseCategory.getName());
            preparedStatement.setInt(2, expenseCategory.getId());

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
    public void delete(int idExpCategory) {
        String sqlDelete = "DELETE FROM EXPENSE_CATEGORY WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
            preparedStatement.setInt(1, idExpCategory);

            int deleteComplete = preparedStatement.executeUpdate();

            if (deleteComplete > 0) {
                System.out.println("The expense category was successfully delete from the database.");
            } else {
                System.out.println("The expense category couldn´t be found into the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
