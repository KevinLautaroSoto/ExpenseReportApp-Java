import implementations.MenuCRUDImpl;
import menu.MenuCRUD;
import entities.Expense;
import entities.ExpenseCategory;
import implementations.ExpenseCalculatorImpl;
import interfaces.ExpenseCalculator;
import config.JdbcConfig;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        JdbcConfig.createTable();// Crea las tablas en la base de datos de H2.

        ExpenseCalculator expenseCalculator = new ExpenseCalculatorImpl();

        MenuCRUD menuCRUD = new MenuCRUDImpl();

        List<Expense> expenseList = new ArrayList<>(); //Lista que almacenara todos los gastos
        List<ExpenseCategory> expenseCategoryList = new ArrayList<>();
        int option;//es la variables que almacena la opcion del menu.

        //Agrego algunas categorias para probar funcionalidades rapidamente.
        addCategories(expenseCategoryList);

        //Agrego algunos datos a la lista para ir probando las funcionalidades.
        addExpenses(expenseList, expenseCategoryList);
        int countExpense = 2;

        do {
            /*System.out.println("MENU:");
            System.out.println("1- Lista de Gastos.");
            System.out.println("2- Ingresar nuevo Gasto.");
            System.out.println("3- Lista de Categorias de Gastos.");
            System.out.println("4- Ingresar nueva Categoria de Gasto.");
            System.out.println("5- Filtrar Gastos por categoria.");
            System.out.println("9- Salir.");
            System.out.println(" "); */

            //Nuevo menu:
            System.out.println("""
                            MENU:\s
                             1- Lista de Gastos.
                             2- Crear nuevo Gasto.
                             3- Actualizar un Gasto.
                             4- Eliminar un Gasto.
                             5- Lista de categorias de Gastos.
                             6- Crear nueva categoria de Gasto.
                             7- Actualizar una categoria de Gasto.
                             8- Eliminar una categoria de Gasto.
                             9- Filtrar Gastos por categoria.
                             10- Salir.""" +
                    "\n------------------------------------------------------------------------------\n");

            System.out.print("Ingrese la opcion: ");
            option = scanner.nextInt();

            //un Switch para que se ejecuten distintos codigos segun que vaya eligiendo el usuario en consola.
            switch (option) {
                case 1:
                    //Menu.option1(expenseList); Esta es la manera sin el CRUD ni la base de datos.
                    menuCRUD.option1();
                    System.out.println(" ");
                    break;
                case 2:
                    //Menu.option2(expenseList, expenseCategoryList, countExpense);
                    menuCRUD.option2();
                    break;
                case 3:
                    menuCRUD.option3();
                    break;
                case 4:
                    menuCRUD.option4();
                    break;
                case 5:
                    //Menu.Option3(expenseCategoryList);
                    menuCRUD.option5();
                    break;
                case 6:
                    //Menu.Option4(expenseCategoryList);
                    menuCRUD.option6();
                    break;
                case 7:
                    menuCRUD.option7();
                    break;
                case 8:
                    menuCRUD.option8();
                    break;
                case 9:
                    //Menu.Option5(expenseCategoryList, expenseList);
                    menuCRUD.option9();
                    break;
                case 10:
                    System.out.println("Saliendo del sistema...");
                    break;
            }

        }while(option != 10);


    }

    public static void addCategories(List<ExpenseCategory> expenseCategoryList) {
        ExpenseCategory comida = new ExpenseCategory("comida");
        expenseCategoryList.add(comida);
        ExpenseCategory ropa = new ExpenseCategory("ropa");
        expenseCategoryList.add(ropa);
        ExpenseCategory entretenimiento = new ExpenseCategory("entretenimiento");
        expenseCategoryList.add(entretenimiento);
        ExpenseCategory impuesto = new ExpenseCategory("impuesto");
        expenseCategoryList.add(impuesto);
    }

    public static void addExpenses(List<Expense> expenseList, List<ExpenseCategory> expenseCategoryList) {
        Expense expense1 = new Expense(54.4, "cena", expenseCategoryList.get(0), "01 Enero 2024");
        Expense expense2 = new Expense(120.73, "remera de adidas", expenseCategoryList.get(1), "04 Enero 2024");
        Expense expense3 = new Expense(4.50, "cine", expenseCategoryList.get(2), "11 Enero 2024");
        expenseList.add(expense1);
        expenseList.add(expense2);
        expenseList.add(expense3);
    }
}