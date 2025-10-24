package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.entity.snake.Snake;
import ru.neikatya.gamesnake.service.FieldService;
import ru.neikatya.gamesnake.service.SnakeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnakeServiceTest {

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private FieldService fieldService;

    @InjectMocks
    private SnakeService snakeService;

    private Field mockField;
    private Snake mockSnake;

    @BeforeEach
    void setUp() {
        mockField = mock(Field.class);
        mockSnake = mock(Snake.class);
    }

    @Test
    public void testTurnTo() {
        Direction direction = Direction.RIGHT;
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);
        doNothing().when(mockSnake).turnTo(direction);

        snakeService.turnTo(direction);

        verify(fieldService).findInGameField();
        verify(mockField).getSnake();
        verify(mockSnake).turnTo(direction);
        verify(fieldRepository).save(mockField);
    }

    @Test
    public void testTurnTo_LeftDirection() {
        Direction direction = Direction.LEFT;
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);
        doNothing().when(mockSnake).turnTo(direction);

        snakeService.turnTo(direction);

        verify(fieldService).findInGameField();
        verify(mockField).getSnake();
        verify(mockSnake).turnTo(direction);
        verify(fieldRepository).save(mockField);
    }

    @Test
    public void testTurnTo_UpDirection() {
        Direction direction = Direction.UP;
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);
        doNothing().when(mockSnake).turnTo(direction);

        snakeService.turnTo(direction);

        verify(fieldService).findInGameField();
        verify(mockField).getSnake();
        verify(mockSnake).turnTo(direction);
        verify(fieldRepository).save(mockField);
    }

    @Test
    public void testTurnTo_DownDirection() {
        Direction direction = Direction.DOWN;
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);
        doNothing().when(mockSnake).turnTo(direction);

        snakeService.turnTo(direction);

        verify(fieldService).findInGameField();
        verify(mockField).getSnake();
        verify(mockSnake).turnTo(direction);
        verify(fieldRepository).save(mockField);
    }

    @Test
    public void testGetSnake() {
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);

        Snake result = snakeService.getSnake();

        assertEquals(mockSnake, result);
        verify(fieldService).findInGameField();
        verify(mockField).getSnake();
        verify(fieldRepository, never()).save(any());
    }

    @Test
    public void testConstructor() {
        FieldRepository repository = mock(FieldRepository.class);
        FieldService service = mock(FieldService.class);

        SnakeService snakeService = new SnakeService(repository, service);

        assertNotNull(snakeService);
    }

    @Test
    public void testTurnTo_MultipleCalls() {
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);
        doNothing().when(mockSnake).turnTo(any(Direction.class));

        snakeService.turnTo(Direction.LEFT);
        snakeService.turnTo(Direction.RIGHT);
        snakeService.turnTo(Direction.UP);

        verify(fieldService, times(3)).findInGameField();
        verify(mockField, times(3)).getSnake();
        verify(mockSnake).turnTo(Direction.LEFT);
        verify(mockSnake).turnTo(Direction.RIGHT);
        verify(mockSnake).turnTo(Direction.UP);
        verify(fieldRepository, times(3)).save(mockField);
    }

    @Test
    public void testGetSnake_MultipleCalls() {
        when(fieldService.findInGameField()).thenReturn(mockField);
        when(mockField.getSnake()).thenReturn(mockSnake);

        Snake result1 = snakeService.getSnake();
        Snake result2 = snakeService.getSnake();

        assertEquals(mockSnake, result1);
        assertEquals(mockSnake, result2);
        verify(fieldService, times(2)).findInGameField();
        verify(mockField, times(2)).getSnake();
        verify(fieldRepository, never()).save(any());
    }

    @Test
    public void testTurnTo_AllDirections() {
        Direction[] directions = Direction.values();

        for (Direction direction : directions) {
            when(fieldService.findInGameField()).thenReturn(mockField);
            when(mockField.getSnake()).thenReturn(mockSnake);
            doNothing().when(mockSnake).turnTo(direction);

            snakeService.turnTo(direction);

            verify(mockSnake).turnTo(direction);

            reset(mockSnake);
        }

        verify(fieldService, times(directions.length)).findInGameField();
        verify(fieldRepository, times(directions.length)).save(mockField);
    }
}