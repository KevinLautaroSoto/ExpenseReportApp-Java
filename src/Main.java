import entities.Expense;
import implementations.ExpenseCalculatorImplementation;
import interfaces.ExpenseCalculator;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ExpenseCalculator expenseCalculator = new ExpenseCalculatorImplementation();
    }
}