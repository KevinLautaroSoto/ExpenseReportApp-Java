package implementations;

import entities.Expense;
import interfaces.ExpenseCalculator;
import java.util.List;

public class ExpenseCalculatorImplementation implements ExpenseCalculator {
    @Override
    public double calculateExpense(Expense expense) {return expense.getAmount();}

    @Override
    public double calculateTotalExpense(List<Expense> expenseList) {
        return expenseList.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
