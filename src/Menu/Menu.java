package Menu;

import entities.Expense;
import entities.ExpenseCategory;
import implementations.CategoryFilter;

import java.util.List;
import java.util.Scanner;

public class Menu {
    public static void option1(List<Expense> expenseList) {
        System.out.println("Lista de gastos: ");
        int cont = 1;
        for (Expense expense : expenseList) {
            System.out.println(cont + ": " + expense.getDescription() + " = $" + expense.getAmount() + ", Category: " + expense.getCategory().getName());
            cont++;
        }
    }

    public static void option2(List<Expense> expenseList, List<ExpenseCategory> expenseCategoryList, int countExpenses) {
        Scanner scanner = new Scanner(System.in);
        int countDescAux = 1;
        System.out.println("Ingrese un nuevo gasto: ");

        //Obtengo el index de la categoria del gasto.
        System.out.println("\n Seleccione la categoria del gasto: ");
        for (ExpenseCategory expenseCategory : expenseCategoryList) {
            System.out.println(countDescAux + " " + expenseCategory.getName());
            countDescAux++;
        }
        int catScan = scanner.nextInt() - 1;
        scanner.nextLine();

        //Ingreso de la descripcion del gasto.
        System.out.println("Ingrese la descripcion del gasto: ");
        String descScan = scanner.nextLine();


        //Ingreso del monto del gasto. Convierto el monto ingresado en teclado como String a Double.
        Double amountAux = null;
        while (amountAux == null) {
            System.out.println("Ingrese el monto del gasto");
            String amountString = scanner.nextLine();
            try {
                amountAux = Double.parseDouble(amountString);
            } catch (NumberFormatException e) {
                System.out.println("Error. Ingrese un monto válido");
            }
        }

        //Ingreso de la fecha del gasto.
        System.out.println("Ingrese la fecha del gasto: ");
        String dateScan = scanner.nextLine();

        Expense expenseAux =new Expense(amountAux, descScan, expenseCategoryList.get(catScan), dateScan);
        expenseList.add(expenseAux);

        System.out.println("Gasto ingresado con exito!!");
        //scanner.close();
    }

    public static void Option3(List<ExpenseCategory> expenseCategoryList) {
        int countDescAux = 1;
        for (ExpenseCategory expenseCategory : expenseCategoryList) {
            System.out.println(countDescAux + " " + expenseCategory.getName());
            countDescAux++;
        }
        System.out.println(" ");
    }

    public static void Option4(List<ExpenseCategory> expenseCategoryList) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el nombre de la nueva categoria: ");
        String catScan = scanner.nextLine();
        ExpenseCategory catAux = new ExpenseCategory(catScan);
        expenseCategoryList.add(catAux);
    }

    public static void Option5(List<ExpenseCategory> expenseCategoryList, List<Expense> expenseList) {
        int countDescAux = 1;
        int cont = 1;
        System.out.println("Elija la categoria del filtro: ");
        for (ExpenseCategory expenseCategory : expenseCategoryList) {
            System.out.println(countDescAux + " " + expenseCategory.getName());
            countDescAux++;
        }

        CategoryFilter categoryFilter = new CategoryFilter(expenseCategoryList.get(countDescAux - 1).getName());

        for (Expense expense : expenseList) {
            if(categoryFilter.cumpleFiltro(expense)) {
                System.out.println(cont + ": " + expense.getDescription() + " = $" + expense.getAmount() + ", Category: " + expense.getCategory().getName());
                cont++;
            }
        }
    }
}