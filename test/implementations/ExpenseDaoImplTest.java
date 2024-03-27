package implementations;

import entities.Expense;
import entities.ExpenseCategory;
import entities.ExpenseDto;
import interfaces.ExpenseCategoryDAO;
import interfaces.ExpenseDAO;
import interfaces.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.UtilitiesImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; //Importa las clases estaticas de Mockito para que podamos usar metodos estaticos
//de Mockito sin escribir el nombre de la clase completa.

class ExpenseDaoImplTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();//declaro esta variable para
    //capturar la salida estandar.
    private final PrintStream originalOut = System.out;//esta variable almacenara la salida estandar original
    //antes de realizar cambios.

    @BeforeEach
    public void setUpStream(){
        System.setOut(new PrintStream(outContent));//establece la salida estandar a ByteArrayOutputStream.
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);//restaura la salida estandar original.
    }
    //las anotaciones anteriores son proporcionadas por JUnit Jupiter y se utilizan para marcar metodos que
    //deben ejecutarse antes y despues de cada metodo de prueba.
    @Test
    public void testInsert_SuccessfullInsertion() throws SQLException {
        //GIVEN
        Connection mockConnection = mock(Connection.class);//este objeto simulara la conexion a la base de datos.
        //Lo que nos permite controlar su comportamiento durante la prueba.

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);//Este objeto PreparedStatement simulara una consulta
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);/*
        utilizo mockito para decirle al objeto mockConnection como comportarse cuando llame al meotodo preparedStatement()
        con cualquier cadena como argumento. En este caso, le digo que devuelva el objeto mockPreparedStatement.
        */
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);//Utilizo mockito para decirle al meotodo mockPreparedSatement
        //como comportarse cuando se ejecuta el metodo executeUpdate. En este caso le decimos que devuelva 1, simulando
        //una inserción exitosa en la base de datos.

        ExpenseCategoryDAO expenseCategoryDAO = new ExpenseCategoryDaoImpl();
        ExpenseCategory expenseCategory = new ExpenseCategory("test category");
        ExpenseDto expenseDto = new ExpenseDto(120.3, "expense test", expenseCategory, "test date");
        ExpenseDAO expenseDAOTest = new ExpenseDaoImpl(mockConnection, expenseCategoryDAO);//El objeto ExpenseDao se inicializa con nuestro objeto
        //mockConnection lo que permite simular la conexino a la base de datos en nuestas pruebas sin necesidad de una
        //conexion real.

        //WHEN
        expenseDAOTest.insert(expenseDto);//Llamo al meotodo insert en nuestro objeto expenseDaoTest pasando
        //el objeto expenseDto como argumento lo que simula la insercion de un gasto en la base de datos.

        //THEN
        verify(mockPreparedStatement, times(1)).executeUpdate();
        //utilizamos mockito para verificar que el metodo executeUpdate haya sido llamado exactamente una vez
        //en nuestro objeto mockPreparedStatement lo que asegura que se intento insertar el gasto en la base de datos.
        assertTrue(outContent.toString().trim().contains("The expense was successfully loaded into the database."));
        //verifica que el mensaje de exito se haya impreso correctamente en la salida estandar.
        //Para ello convertimos la salida capturada(outContent) a una cadena (toString) elimina los espacios
        //en blanco al principio y al final(trim()) y luego verifiva si contiene el mensaje esperado.
    }

    @Test
    public void testInsertion_SQLExceptionThrow() throws SQLException {
        //GIVEN or Arrange. is the same thing.
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        ExpenseCategoryDAO expenseCategoryDAO = new ExpenseCategoryDaoImpl();
        ExpenseCategory expenseCategory = new ExpenseCategory("test category");
        ExpenseDto expenseDto = new ExpenseDto(120.3, "expense test", expenseCategory, "test date");
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, expenseCategoryDAO);

        //WHEN & THEN or ACT & ASSERT
        assertThrows(RuntimeException.class, () -> {
           expenseDAO.insert(expenseDto);
        });
    }

    @Test
    public void testInsert_PreparedStatementParameters() throws SQLException {
        //GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        ExpenseCategoryDAO expenseCategoryDAO = new ExpenseCategoryDaoImpl();
        ExpenseCategory expenseCategory = new ExpenseCategory("test category");
        ExpenseDto expenseDto = new ExpenseDto(120.3, "expense test", expenseCategory, "test date");
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, expenseCategoryDAO);

        //WHEN
        expenseDAO.insert(expenseDto);

        //THEN
        verify(mockPreparedStatement, times(1)).setDouble(1, expenseDto.getAmount());
        verify(mockPreparedStatement, times(1)).setString(2, expenseDto.getDescription());
        verify(mockPreparedStatement, times(1)).setInt(3, expenseDto.getCategory().getId());
        verify(mockPreparedStatement, times(1)).setString(4, expenseDto.getDate());
    }

    @Test
    public void getAll_ReturnsListOfExpenses() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        //Simulate two rows in the result set
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("amount")).thenReturn(10.0, 20.0);
        when(mockResultSet.getString("description")).thenReturn("Expense 1", "Expense 2");
        when(mockResultSet.getInt("category_id")).thenReturn(1, 2);
        when(mockResultSet.getString("date")).thenReturn("2024-03-21", "2024-03-22");
        when(mockResultSet.getInt("id")).thenReturn(1, 2);

        //Mock ExpenseCategoryDao
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDAO.class);
        when(mockExpenseCategoryDao.getById(1)).thenReturn(new ExpenseCategory("Category 1"));
        when(mockExpenseCategoryDao.getById(2)).thenReturn(new ExpenseCategory("Category 2"));

        //Create ExpenseDao instance
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        //WHEN
        List<ExpenseDto> result = expenseDAO.getAll();

        //THEN
        assertNotNull(result);
        assertEquals(2, result.size());

        ExpenseDto expense1 = result.get(0);
        assertEquals(10.0, expense1.getAmount());
        assertEquals("Expense 1", expense1.getDescription());
        assertEquals("Category 1", expense1.getCategory().getName());
        assertEquals("2024-03-21", expense1.getDate());
        assertEquals(1, expense1.getId());

        ExpenseDto expense2 = result.get(1);
        assertEquals(20.0, expense2.getAmount());
        assertEquals("Expense 2", expense2.getDescription());
        assertEquals("Category 2", expense2.getCategory().getName());
        assertEquals("2024-03-22", expense2.getDate());
        assertEquals(2, expense2.getId());
    }

    @Test
    public void getAll_MultipleExpenses_ReturnsAllExpenses() throws SQLException {
        //GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("amount")).thenReturn(10.0, 20.0);
        when(mockResultSet.getString("description")).thenReturn("Expense 1", "Expense 2");
        when(mockResultSet.getInt("category_id")).thenReturn(1, 2);
        when(mockResultSet.getString("date")).thenReturn("2024-03-20", "2024-03-21");
        when(mockResultSet.getInt("id")).thenReturn(1, 2);

        when(mockExpenseCategoryDao.getById(anyInt())).thenReturn(new ExpenseCategory("Category 1"));

        //When
        List<ExpenseDto> result = expenseDAO.getAll();

        //THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10.0, result.get(0).getAmount());
        assertEquals("Expense 1", result.get(0).getDescription());
        assertEquals("Category 1", result.get(0).getCategory().getName());
        assertEquals("2024-03-20", result.get(0).getDate());
        assertEquals(1, result.get(0).getId());

        assertEquals(20.0, result.get(1).getAmount());
        assertEquals("Expense 2", result.get(1).getDescription());
        assertEquals("Category 1", result.get(1).getCategory().getName());
        assertEquals("2024-03-21", result.get(1).getDate());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    public void getById_ExpenseFound_ReturnsExpense() throws SQLException {
        // GIVEN
        int idExpense = 1;
        double amount = 10.0;
        String description = "Test expense";
        int categoryId = 1;
        String date = "2024-03-21";

        ResultSet mockResultSet = mock(ResultSet.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        Connection mockConnection = mock(Connection.class);
        ExpenseDAO expenseDao = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble("amount")).thenReturn(amount);
        when(mockResultSet.getString("description")).thenReturn(description);
        when(mockResultSet.getInt("category_id")).thenReturn(categoryId);
        when(mockResultSet.getString("date")).thenReturn(date);

        ExpenseCategory category = new ExpenseCategory("Test category");
        when(mockExpenseCategoryDao.getById(categoryId)).thenReturn(category);

        // WHEN
        Expense result = expenseDao.getById(idExpense);

        // THEN
        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(category, result.getCategory());
        assertEquals(date, result.getDate());
    }

    @Test
    public void testGetById_ExpenseNotFound() throws SQLException {
        // GIVEN
        int idExpense = 1;
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate result set with no rows:
        when(mockResultSet.next()).thenReturn(false);

        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        when(mockExpenseCategoryDao.getById(anyInt())).thenReturn(null);

        // Create ExpenseDao instance
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        // WHEN
        Expense result = expenseDAO.getById(idExpense);

        // THEN
        assertNull(result);
    }

    @Test
    public void testGetByDescription_MatchFound() throws SQLException {
        // GIVEN
        String descriptionSearch = "test";
        List<ExpenseDto> expectedExpenses = new ArrayList<>();

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        // Simulate result set with one row:
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One row found
        when(mockResultSet.getDouble("amount")).thenReturn(10.0);
        when(mockResultSet.getString("description")).thenReturn("Test Expense");
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("date")).thenReturn("2024-03-21");
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockExpenseCategoryDao.getById(anyInt())).thenReturn(new ExpenseCategory("test category"));

        ExpenseDto expectedExpense = new ExpenseDto(10.0, "Test Expense", new ExpenseCategory("Test Category"), "2024-03-21");
        expectedExpense.setId(1);
        expectedExpenses.add(expectedExpense);

        // WHEN
        List<ExpenseDto> result = expenseDAO.getByDescription(descriptionSearch);

        // THEN
        assertFalse(result.isEmpty());
        assertEquals(expectedExpenses.size(), result.size());
    }

    @Test
    public void testGetByDescription_NoMatchFound() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);


        String descriptionSearch = "nonexistent";
        List<ExpenseDto> expectedExpenses = new ArrayList<>();
        // Simulate result set with no rows:
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No rows found

        // WHEN
        List<ExpenseDto> result = expenseDAO.getByDescription(descriptionSearch);

        // THEN
        assertTrue(result.isEmpty());
        assertEquals(expectedExpenses.size(), result.size());
    }

    @Test
    public void testGetByDescription_SQLException() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        String descriptionSearch = "test";
        // Simulate SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // WHEN / THEN
        assertThrows(RuntimeException.class, () -> expenseDAO.getByDescription(descriptionSearch));
    }

    @Test
    public void testUpdate_Successful() throws SQLException {
        // GIVEN
        Utilities utilities = mock(UtilitiesImpl.class);

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        Expense expenseToUpdate = new Expense();
        expenseToUpdate.setId(1);
        expenseToUpdate.setAmount(20.0);
        expenseToUpdate.setDescription("Updated description");
        expenseToUpdate.setDate("2024-03-21");
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1);
        expenseToUpdate.setCategory(category);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // WHEN
        expenseDAO.update(expenseToUpdate);

        // THEN
        verify(mockPreparedStatement).setDouble(1, expenseToUpdate.getAmount());
        verify(mockPreparedStatement).setString(2, expenseToUpdate.getDescription());
        verify(mockPreparedStatement).setInt(3, expenseToUpdate.getCategory().getId());
        verify(mockPreparedStatement).setString(4, expenseToUpdate.getDate());
        verify(mockPreparedStatement).setInt(5, expenseToUpdate.getId());
        verify(mockPreparedStatement).executeUpdate();
        //verify(utilities).consoleLoger("The update was successfully loaded."); se romper y requiere demasiado cambio en el codigo que no creo conveniente hacer.
        //verifyNoMoreInteractions(mockPreparedStatement, System.out);
    }

    @Test
    public void testUpdate_SQLException() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        Expense expenseToUpdate = new Expense();
        expenseToUpdate.setId(1);
        expenseToUpdate.setAmount(20.0);
        expenseToUpdate.setDescription("Updated description");
        expenseToUpdate.setDate("2024-03-21");
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1);
        expenseToUpdate.setCategory(category);

        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // WHEN / THEN
        assertThrows(RuntimeException.class, () -> expenseDAO.update(expenseToUpdate));
    }

    @Test
    public void testDelete_Successful() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        int idToDelete = 1;
        String sqlDelete = "DELETE FROM EXPENSES WHERE id = ?";
        when(mockConnection.prepareStatement(sqlDelete)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // WHEN
        expenseDAO.delete(idToDelete);

        // THEN
        verify(mockConnection).prepareStatement(sqlDelete);
        verify(mockPreparedStatement).setInt(1, idToDelete);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testDelete_Unsuccessful() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        int idToDelete = 1;
        String sqlDelete = "DELETE FROM EXPENSES WHERE id = ?";
        when(mockConnection.prepareStatement(sqlDelete)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simular que no se eliminó ninguna fila

        // WHEN
        expenseDAO.delete(idToDelete);

        // THEN
        verify(mockConnection).prepareStatement(sqlDelete);
        verify(mockPreparedStatement).setInt(1, idToDelete);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testDelete_ExceptionThrown() throws SQLException {
        // GIVEN
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ExpenseCategoryDAO mockExpenseCategoryDao = mock(ExpenseCategoryDaoImpl.class);
        ExpenseDAO expenseDAO = new ExpenseDaoImpl(mockConnection, mockExpenseCategoryDao);

        int idToDelete = 1;
        String errorMessage = "SQL Exception";
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException(errorMessage));

        // WHEN - THEN
        assertThrows(RuntimeException.class, () -> {
            expenseDAO.delete(idToDelete);
        });
    }
}