package utilities;

import entities.Expense;
import entities.ExpenseCategory;
import entities.ExpenseCategoryDto;
import entities.ExpenseDto;
import interfaces.ExpenseCategoryDAO;
import interfaces.PrintableList;
import java.util.List;

public class  Utilities<T> implements PrintableList<T> {
    @Override
    public void printList(List<T> list) {
        for (T item : list) {
            if (item instanceof ExpenseDto expense) {
                System.out.println(expense.getId() + ": " + expense.getDescription() + " = $" + expense.getAmount() + ", Category: " + expense.getCategory().getName());
            } else if (item instanceof ExpenseCategoryDto category) {
                System.out.println(category.getId() + ": " + category.getName());
            }
        }
    }
}

