package implementations;

import entities.ExpenseCategory;
import entities.ExpenseDto;
import interfaces.ExpenseCategoryDAO;
import interfaces.ExpenseDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    void getById() {
    }

    @Test
    void getByDescription() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}