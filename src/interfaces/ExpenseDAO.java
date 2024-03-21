package interfaces;

import entities.Expense;
import entities.ExpenseDto;

import java.util.List;
public interface ExpenseDAO {
    void insert (ExpenseDto expenseDto);

    List<ExpenseDto> getAll();

    Expense getById(int idExpense);
    List<ExpenseDto> getByDescription(String description);

    void update (Expense expense);

    void delete (int id);
}
