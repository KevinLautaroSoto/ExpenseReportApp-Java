package interfaces;

import entities.ExpenseCategory;
import entities.ExpenseCategoryDto;

import java.util.List;

public interface ExpenseCategoryDAO {
    void insert(ExpenseCategoryDto expenseCategoryDto);

    List<ExpenseCategoryDto> getAll();

    ExpenseCategory getById(int idExpCategory);

    List<ExpenseCategoryDto> getByName (String nameCategory);

    void update (ExpenseCategory expenseCategory);

    void delete (int idExpCategory);
}
