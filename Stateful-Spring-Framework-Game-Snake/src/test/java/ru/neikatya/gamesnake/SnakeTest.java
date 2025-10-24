package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.neikatya.gamesnake.entity.apple.Apple;
import ru.neikatya.gamesnake.entity.Head;
import ru.neikatya.gamesnake.entity.Tail;
import ru.neikatya.gamesnake.entity.snake.Snake;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnakeTest {

    private Snake snake;
    private Head mockHead;
    private Tail mockTail;

    @BeforeEach
    void setUp() {
        mockHead = mock(Head.class);
        mockTail = mock(Tail.class);
        snake = new Snake();
        snake.setId(UUID.randomUUID());
        snake.setHead(mockHead);
        snake.setTail(mockTail);
        snake.setVersion(1L);
    }

    @Test
    public void testCreateSnake() {
        Snake result = Snake.createSnake();

        assertNotNull(result);
        assertNull(result.getId());
        assertNotNull(result.getHead());
        assertNotNull(result.getTail());
        assertEquals(1, result.getStepLength());
        assertNull(result.getVersion());
    }

    @Test
    public void testMoveRight() {
        when(mockHead.getDirection()).thenReturn(Direction.RIGHT);

        Vector result = snake.move();

        assertNotNull(result);
        assertEquals(1, result.x());
        assertEquals(0, result.y());
    }

    @Test
    public void testMoveLeft() {
        when(mockHead.getDirection()).thenReturn(Direction.LEFT);

        Vector result = snake.move();

        assertNotNull(result);
        assertEquals(-1, result.x());
        assertEquals(0, result.y());
    }

    @Test
    public void testMoveUp() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        Vector result = snake.move();

        assertNotNull(result);
        assertEquals(0, result.x());
        assertEquals(-1, result.y());
    }

    @Test
    public void testMoveDown() {
        when(mockHead.getDirection()).thenReturn(Direction.DOWN);

        Vector result = snake.move();

        assertNotNull(result);
        assertEquals(0, result.x());
        assertEquals(1, result.y());
    }

    @Test
    public void testGetLength() {
        when(mockTail.getLength()).thenReturn(3);

        int result = snake.getLength();

        assertEquals(4, result);
    }

    @Test
    public void testEating() {
        Apple mockApple = mock(Apple.class);
        when(mockApple.getSatiety()).thenReturn(2);

        snake.eating(mockApple);

        verify(mockTail).increase(2);
    }

    @Test
    public void testTurnTo_RightTurn() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        snake.turnTo(Direction.RIGHT);

        verify(mockHead, times(1)).turnRight();
        verify(mockHead, never()).turnLeft();
    }

    @Test
    public void testTurnTo_LeftTurn() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        snake.turnTo(Direction.LEFT);

        verify(mockHead, times(1)).turnLeft();
        verify(mockHead, never()).turnRight();
    }

    @Test
    public void testTurnTo_SameDirection() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        assertThrows(IllegalStateException.class, () -> {
            snake.turnTo(Direction.UP);
        });
    }

    @Test
    public void testTurnTo_OppositeDirection() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        assertThrows(IllegalStateException.class, () -> {
            snake.turnTo(Direction.DOWN);
        });
    }

    @Test
    public void testTurnTo_RightToDown() {
        when(mockHead.getDirection()).thenReturn(Direction.RIGHT);

        snake.turnTo(Direction.DOWN);

        verify(mockHead, times(1)).turnRight();
        verify(mockHead, never()).turnLeft();
    }

    @Test
    public void testTurnTo_DownToLeft() {
        when(mockHead.getDirection()).thenReturn(Direction.DOWN);

        snake.turnTo(Direction.LEFT);

        verify(mockHead, times(1)).turnRight();
        verify(mockHead, never()).turnLeft();
    }

    @Test
    public void testTurnTo_LeftToUp() {
        when(mockHead.getDirection()).thenReturn(Direction.LEFT);

        snake.turnTo(Direction.UP);

        verify(mockHead, times(1)).turnRight();
        verify(mockHead, never()).turnLeft();
    }

    @Test
    public void testTurnTo_UpToLeft() {
        when(mockHead.getDirection()).thenReturn(Direction.UP);

        snake.turnTo(Direction.LEFT);

        verify(mockHead, times(1)).turnLeft();
        verify(mockHead, never()).turnRight();
    }

    @Test
    public void testTurnTo_RightToUp() {
        when(mockHead.getDirection()).thenReturn(Direction.RIGHT);

        snake.turnTo(Direction.UP);

        verify(mockHead, times(1)).turnLeft();
        verify(mockHead, never()).turnRight();
    }

    @Test
    public void testTurnTo_DownToRight() {
        when(mockHead.getDirection()).thenReturn(Direction.DOWN);

        snake.turnTo(Direction.RIGHT);

        verify(mockHead, times(1)).turnLeft();
        verify(mockHead, never()).turnRight();
    }
}