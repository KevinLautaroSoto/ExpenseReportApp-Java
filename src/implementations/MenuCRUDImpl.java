package implementations;

import config.JdbcConfig;
import entities.Expense;
import entities.ExpenseCategory;
import entities.ExpenseCategoryDto;
import entities.ExpenseDto;
import interfaces.ExpenseCategoryDAO;
import interfaces.ExpenseDAO;
import interfaces.PrintableList;
import menu.MenuCRUD;
import utilities.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuCRUDImpl implements MenuCRUD {

    private final Connection connection = JdbcConfig.getConnection();
    Scanner scanner = new Scanner(System.in);
    ExpenseDAO expenseDao = new ExpenseDaoImpl();
    ExpenseCategoryDAO expenseCatDAO = new ExpenseCategoryDaoImpl();

    @Override
    //Lista de Gastos cargados en la base de datos.
    public void option1() {
        List<ExpenseDto> allExpenses = expenseDao.getAll();
        PrintableList<ExpenseDto> expensePrinter = new Utilities<>();
        expensePrinter.printList(allExpenses);
    }

    @Override
    //Ingresar un nuevo gasto en la base de datos.
    public void option2() {
        System.out.println("Ingrese un nuevo gasto: ");

        //Obtengo el index de la categoria del gasto.
        System.out.println("\n Seleccione la categoria del gasto: ");
        PrintableList<ExpenseCategoryDto> categoryPrinter = new Utilities<>();
        categoryPrinter.printList(expenseCatDAO.getAll());

        int catScan = scanner.nextInt();
        scanner.nextLine();

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

        ExpenseDto newExpense = new ExpenseDto();

        ExpenseCategory expCatAux = expenseCatDAO.getById(catScan);

        newExpense.setAmount(amountAux);
        newExpense.setDescription(descScan);
        newExpense.setDate(dateScan);
        newExpense.setCategory(expCatAux);
        expenseDao.insert(newExpense);
    }

    @Override
    //Actualiza el valor de un gasto ya existente en la base de datos.
    public void option3() {
        System.out.println("Proporcione la descripcion del gasto que quiera cambiar sus valores.");
        String descriptionSearch = scanner.nextLine();
        List<ExpenseDto> expenseDtosSearch = expenseDao.getByDescription(descriptionSearch);

        if (expenseDtosSearch.isEmpty()) {
            System.out.println("La lista de gastos está vacía.");
            return;
        }


        System.out.println("El gasto a cambiar es: ");

        PrintableList<ExpenseDto> expensePrinter = new Utilities<>();
        expensePrinter.printList(expenseDtosSearch);


        System.out.println("Ingrese los datos nuevos a guardar en el gasto: ");

        //Obtengo el index de la categoria del gasto.
        System.out.println("\n Seleccione la categoria del gasto: ");
        PrintableList<ExpenseCategoryDto> categoryPrinter = new Utilities<>();
        categoryPrinter.printList(expenseCatDAO.getAll());

        int catScan = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese la nueva descripcion del gasto: ");
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



        if (!expenseDtosSearch.isEmpty()) {
            Expense expenseUpdate = new Expense();
            expenseUpdate.setAmount(amountAux);
            expenseUpdate.setDate(dateScan);
            expenseUpdate.setDescription(descScan);
            expenseUpdate.setCategory(expenseCatDAO.getById(catScan));

            ExpenseDto expenseDtoToUpdate = expenseDtosSearch.getFirst(); // Obtiene el primer elemento de la lista
            expenseUpdate.setId(expenseDtoToUpdate.getId());

            expenseDao.update(expenseUpdate);
        } else {
            System.out.println("La lista de gastos está vacía o la descripción proporcionada no coincide con ningún gasto.");
            // Puedes agregar más lógica aquí según sea necesario
        }
    }

    @Override
    //Elimina un gasto de la base de datos.
    public void option4() {
        option1();
        System.out.println("Segun esta lista, porporcine el id del gasto que quiere eliminar.");
        int idDelete = scanner.nextInt();
        expenseDao.delete(idDelete);
    }

    @Override
    //Lista de Categorias de Gastos.
    public void option5() {
        PrintableList<ExpenseCategoryDto> categoryPrinter = new Utilities<>();
        categoryPrinter.printList(expenseCatDAO.getAll());
        System.out.println(" ");
    }

    @Override
    //Ingresar una nueva categoria de Gastos a la base de datos.
    public void option6() {
        System.out.println("Ingrese el nombre de la nueva categoria: ");
        String catScan = scanner.nextLine();
        ExpenseCategoryDto expenseCat = new ExpenseCategoryDto();
        expenseCat.setName(catScan);
        expenseCatDAO.insert(expenseCat);
    }

    @Override
    //Actualiza el nombre de una categoria de gasto existente en la base de datos.
    public void option7() {
        option5();
        System.out.println("De la lista de categorias proporcinada, escriba cual quiere eliminar: ");
        String catDelete = scanner.nextLine();
        List<ExpenseCategoryDto> expenseSearchByName = expenseCatDAO.getByName(catDelete);

        if(expenseSearchByName.isEmpty()) {
            System.out.println("No existe una categoria con ese nombre.");
            return;
        }

        ExpenseCategoryDto categoryFind = expenseSearchByName.getFirst();
        System.out.println("La categoria a modificar es: ");
        PrintableList<ExpenseCategoryDto> printCategories = new Utilities<>();
        printCategories.printList(expenseSearchByName);

        System.out.println("Indique el nuevo nombre de la categoria: ");
        String newCategName = scanner.nextLine();

        ExpenseCategory categUpdate = new ExpenseCategory();
        categUpdate.setId(categoryFind.getId());
        categUpdate.setName(newCategName);

        expenseCatDAO.update(categUpdate);
    }

    @Override
    //Elimina una categoria de gasto almacenada en la base de datos.
    public void option8() {
        option5();
        System.out.println("Segun la lista ingrese el id de la categoria que quiere eliminar.");
        int idDelete = scanner.nextInt();
        expenseCatDAO.delete(idDelete);
    }

    @Override
    //Filtrar Gastos por su categoria.
    public void option9() {
        option5();

        System.out.println("Ingrese el numero de la categoria que quiere filtrar.");
        int numCatExp = scanner.nextInt();

        ExpenseCategory expCatById = expenseCatDAO.getById(numCatExp);

        try {
            String sqlFilter = "SELECT * FROM EXPENSES " +
                    "JOIN EXPENSE_CATEGORY ON EXPENSES.category_id = EXPENSE_CATEGORY.id " +
                    "WHERE EXPENSE_CATEGORY.name = ?";

            //SELECT e.* FROM EXPENSES " +
            //                    "JOIN EXPENSE_CATEGORY ec ON e.category_id = ec.id " +
            //                    "WHERE ec.name = ?

            PreparedStatement preparedStatement = connection.prepareStatement(sqlFilter);
            preparedStatement.setString(1, expCatById.getName());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int contAux = 1;
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                int catId = resultSet.getInt("category_id");
                String catName = expenseCatDAO.getById(catId).getName();
                String date = resultSet.getString("date");

                System.out.println(contAux +": " +
                        "\n Monto: " + amount +
                        "\n Descripcion: " + description +
                        "\n Categoria: " + catName +
                        "\n Fecha: " + date);

                contAux++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


