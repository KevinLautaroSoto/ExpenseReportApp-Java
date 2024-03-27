package utilities;

import entities.ExpenseCategoryDto;
import entities.ExpenseDto;

import java.util.List;

public class UtilitiesImpl<T> implements interfaces.Utilities<T> {
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

    @Override
    public void consoleLoger(String str) {
        System.out.println(str);
    }
}

