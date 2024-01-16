package utilities;

import entities.Expense;
import entities.ExpenseCategory;
import interfaces.PrintableList;
import java.util.List;

public class Utilities<T> implements PrintableList<T> {
    @Override
    public void printList(List<T> list) {
        int cont = 1;
        for (T item : list) {
            if (item instanceof Expense expense) {
                System.out.println(cont + ": " + expense.getDescription() + " = $" + expense.getAmount() + ", Category: " + expense.getCategory().getName());
            } else if (item instanceof ExpenseCategory category) {
                System.out.println(cont + " " + category.getName());
            }
            cont++;
        }
    }
}

