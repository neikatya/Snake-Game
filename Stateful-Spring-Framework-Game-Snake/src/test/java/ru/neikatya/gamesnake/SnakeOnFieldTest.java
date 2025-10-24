package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.neikatya.gamesnake.entity.apple.Apple;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.entity.coordinates.OrderedCoordinate;
import ru.neikatya.gamesnake.entity.snake.Snake;
import ru.neikatya.gamesnake.entity.snake.SnakeOnField;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnakeOnFieldTest {

    private SnakeOnField snakeOnField;
    private Snake mockSnake;
    private List<Coordinates> testCoordinates;
    private List<OrderedCoordinate> testOrderedCoordinates;

    @BeforeEach
    void setUp() {
        mockSnake = mock(Snake.class);
        testCoordinates = List.of(
                new Coordinates(2, 2),
                new Coordinates(2, 1),
                new Coordinates(2, 0)
        );
        testOrderedCoordinates = List.of(
                new OrderedCoordinate(new Coordinates(2, 0), 0),
                new OrderedCoordinate(new Coordinates(2, 1), -1),
                new OrderedCoordinate(new Coordinates(2, 2), -2)
        );

        snakeOnField = new SnakeOnField();
        snakeOnField.setId(UUID.randomUUID());
        snakeOnField.setSnake(mockSnake);
        snakeOnField.setOrderedCoordinates(new ArrayList<>(testOrderedCoordinates));
        snakeOnField.setVersion(1L);
    }

    @Test
    public void testCreateFrom() {
        SnakeOnField result = SnakeOnField.createFrom(testCoordinates, mockSnake);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(mockSnake, result.getSnake());
        assertEquals(3, result.getOrderedCoordinates().size());
        assertNull(result.getVersion());

        for (int i = 0; i < testCoordinates.size(); i++) {
            assertEquals(testCoordinates.get(i), result.getOrderedCoordinates().get(i).getCoordinate());
            assertEquals(0, result.getOrderedCoordinates().get(i).getOrder());
        }
    }

    @Test
    public void testGetFirstCoordinates() {
        Coordinates firstCoordinates = snakeOnField.getFirstCoordinates();

        assertEquals(new Coordinates(2, 2), firstCoordinates);
    }

    @Test
    public void testEatApple() {
        Apple mockApple = mock(Apple.class);

        snakeOnField.eatApple(mockApple);

        verify(mockSnake).eating(mockApple);
    }

    @Test
    public void testSyncLengthWithCoordinates() {
        when(mockSnake.getLength()).thenReturn(2);
        snakeOnField.setOrderedCoordinates(new ArrayList<>(testOrderedCoordinates));

        snakeOnField.syncLengthWithCoordinates();

        assertEquals(2, snakeOnField.getOrderedCoordinates().size());
        // Должны остаться координаты с наивысшим order (последние в списке)
        assertEquals(new Coordinates(2, 1), snakeOnField.getOrderedCoordinates().get(0).getCoordinate());
        assertEquals(new Coordinates(2, 2), snakeOnField.getOrderedCoordinates().get(1).getCoordinate());
    }

    @Test
    public void testSyncLengthWithCoordinates_NoRemovalNeeded() {
        when(mockSnake.getLength()).thenReturn(3);
        snakeOnField.setOrderedCoordinates(new ArrayList<>(testOrderedCoordinates));

        snakeOnField.syncLengthWithCoordinates();

        assertEquals(3, snakeOnField.getOrderedCoordinates().size());
    }

    @Test
    public void testAddFirstCoordinates() {
        // ВАЖНО: список orderedCoordinates отсортирован по order DESC
        // Первый элемент (getFirst()) имеет НАИБОЛЬШИЙ order
        // В нашем тестовом списке: order = 0, -1, -2
        // Первый элемент: order = 0 (координата (2, 0))

        Coordinates newCoordinates = new Coordinates(3, 2);
        snakeOnField.addFirstCoordinates(newCoordinates);

        assertEquals(4, snakeOnField.getOrderedCoordinates().size());

        // Новая координата должна быть добавлена в конец с order = -1 (0 - 1 = -1)
        OrderedCoordinate addedCoordinate = snakeOnField.getOrderedCoordinates().get(3);
        assertEquals(newCoordinates, addedCoordinate.getCoordinate());
        assertEquals(-1, addedCoordinate.getOrder()); // 0 - 1 = -1
    }

    @Test
    public void testContains() {
        assertTrue(snakeOnField.contains(new Coordinates(2, 2)));
        assertTrue(snakeOnField.contains(new Coordinates(2, 1)));
        assertTrue(snakeOnField.contains(new Coordinates(2, 0)));
        assertFalse(snakeOnField.contains(new Coordinates(5, 5)));
    }

}