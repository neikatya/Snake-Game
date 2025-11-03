package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.entity.apple.AppleOnField;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.entity.coordinates.OrderedCoordinate;
import ru.neikatya.gamesnake.entity.snake.Snake;
import ru.neikatya.gamesnake.entity.snake.SnakeOnField;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FieldTest {

    private Field field;
    private SnakeOnField mockSnakeOnField;
    private Snake mockSnake;
    private AppleOnField mockAppleOnField;

    @BeforeEach
    void setUp() {
        mockSnakeOnField = mock(SnakeOnField.class);
        mockSnake = mock(Snake.class);
        mockAppleOnField = mock(AppleOnField.class);

        field = new Field();
        field.setId(UUID.randomUUID());
        field.setSnakeOnField(mockSnakeOnField);
        field.setApplesOnField(new java.util.ArrayList<>(List.of(mockAppleOnField)));
        field.setSize(10);
        field.setInGame(true);
        field.setVersion(1L);
    }

    @Test
    public void testCreateNewGame() {
        int size = 10;

        Field result = Field.createNewGame(size);

        assertNotNull(result);
        assertNull(result.getId());
        assertNotNull(result.getSnakeOnField());
        assertNotNull(result.getApplesOnField());
        assertEquals(1, result.getApplesOnField().size());
        assertEquals(size, result.getSize());
        assertTrue(result.isInGame());
        assertNull(result.getVersion());
    }

    @Test
    public void testMoveSnake() {
        Vector mockVector = new Vector(1, 0);
        Coordinates mockHead = new Coordinates(5, 5);
        Coordinates mockNewHead = new Coordinates(6, 5);

        when(mockSnakeOnField.getSnake()).thenReturn(mockSnake);
        when(mockSnake.move()).thenReturn(mockVector);
        when(mockSnakeOnField.getFirstCoordinates()).thenReturn(mockHead);
        when(mockAppleOnField.getCoordinates()).thenReturn(new Coordinates(10, 10)); // Different coordinates

        field.moveSnake();

        verify(mockSnakeOnField).addFirstCoordinates(mockNewHead);
        verify(mockSnakeOnField).syncLengthWithCoordinates();
    }

    @Test
    public void testMoveSnake_EatApple() {
        Vector mockVector = new Vector(1, 0);
        Coordinates mockHead = new Coordinates(5, 5);
        Coordinates mockNewHead = new Coordinates(6, 5);

        when(mockSnakeOnField.getSnake()).thenReturn(mockSnake);
        when(mockSnake.move()).thenReturn(mockVector);
        when(mockSnakeOnField.getFirstCoordinates()).thenReturn(mockHead);
        when(mockAppleOnField.getCoordinates()).thenReturn(mockNewHead); // Apple at new head position

        ru.neikatya.gamesnake.entity.apple.Apple mockApple = mock(ru.neikatya.gamesnake.entity.apple.Apple.class);
        when(mockAppleOnField.getApple()).thenReturn(mockApple);

        AppleOnField newApple = mock(AppleOnField.class);
        when(mockSnakeOnField.contains(any())).thenReturn(false);

        field.moveSnake();

        verify(mockSnakeOnField).eatApple(mockApple);
        verify(mockSnakeOnField).addFirstCoordinates(mockNewHead);
        verify(mockSnakeOnField).syncLengthWithCoordinates();

        assertEquals(1, field.getApplesOnField().size());
    }

    @Test
    public void testCheckBorder_ValidCoordinates() {
        Coordinates validCoordinates = new Coordinates(5, 5);

        assertDoesNotThrow(() -> field.checkBorder(validCoordinates));
        assertTrue(field.isInGame()); // Game should still be active
    }

    @Test
    public void testCheckBorder_InvalidX() {
        Coordinates invalidCoordinates = new Coordinates(-1, 5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> field.checkBorder(invalidCoordinates));
        assertEquals("Выход за границу поля", exception.getMessage());
        assertFalse(field.isInGame()); // Game should end
    }

    @Test
    public void testCheckBorder_InvalidY() {
        Coordinates invalidCoordinates = new Coordinates(5, 10);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> field.checkBorder(invalidCoordinates));
        assertEquals("Выход за границу поля", exception.getMessage());
        assertFalse(field.isInGame());
    }

    @Test
    public void testCheckTail_NotInTail() {
        Coordinates coordinates = new Coordinates(3, 3);
        when(mockSnakeOnField.contains(coordinates)).thenReturn(false);

        assertDoesNotThrow(() -> field.checkTail(coordinates));
        assertTrue(field.isInGame());
    }

    @Test
    public void testCheckTail_InTail() {
        Coordinates coordinates = new Coordinates(3, 3);
        when(mockSnakeOnField.contains(coordinates)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> field.checkTail(coordinates));
        assertEquals("Врезание в хвост", exception.getMessage());
        assertFalse(field.isInGame());
    }

    @Test
    public void testAddAppleOnField() {
        AppleOnField newApple = mock(AppleOnField.class);

        field.add(newApple);

        assertTrue(field.getApplesOnField().contains(newApple));
    }

    @Test
    public void testGenerateAppleOnField() {
        when(mockSnakeOnField.contains(any())).thenReturn(false);
        when(mockAppleOnField.getCoordinates()).thenReturn(new Coordinates(1, 1));

        AppleOnField result = field.generateAppleOnField();

        assertNotNull(result);
    }

    @Test
    public void testGenerateAppleOnField_NoFreeSpace() {
        when(mockSnakeOnField.contains(any())).thenReturn(true);
        when(mockAppleOnField.getCoordinates()).thenReturn(new Coordinates(1, 1));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> field.generateAppleOnField());
        assertEquals("Не удалось найти свободную позицию для яблока", exception.getMessage());
    }

    @Test
    public void testGetAppleCoordinates() {
        Coordinates appleCoords = new Coordinates(3, 4);
        when(mockAppleOnField.getCoordinates()).thenReturn(appleCoords);

        List<Coordinates> result = field.getAppleCoordinates();

        assertEquals(1, result.size());
        assertEquals(appleCoords, result.get(0));
    }

    @Test
    public void testGetSnakeCoordinates() {
        OrderedCoordinate mockOrderedCoord = mock(OrderedCoordinate.class);
        Coordinates snakeCoord = new Coordinates(5, 5);
        when(mockOrderedCoord.getCoordinate()).thenReturn(snakeCoord);
        when(mockSnakeOnField.getOrderedCoordinates()).thenReturn(List.of(mockOrderedCoord));

        List<Coordinates> result = field.getSnakeCoordinates();

        assertEquals(1, result.size());
        assertEquals(snakeCoord, result.get(0));
    }

    @Test
    public void testGetSnakeCoordinates_NoSnake() {
        field.setSnakeOnField(null);

        List<Coordinates> result = field.getSnakeCoordinates();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testEndGame() {
        field.endGame();

        assertFalse(field.isInGame());
    }

    @Test
    public void testGetSnake() {
        when(mockSnakeOnField.getSnake()).thenReturn(mockSnake);

        Snake result = field.getSnake();

        assertEquals(mockSnake, result);
    }
}