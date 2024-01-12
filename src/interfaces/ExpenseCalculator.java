package interfaces;
import entities.Expense;
import java.util.List;

public interface ExpenseCalculator {
    double calculateExpense(Expense expense);

    double calculateTotalExpense(List<Expense> expenseList);

}
