package ru.neikatya.gamesnake.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neikatya.gamesnake.FieldRepository;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @Mock
    private FieldRepository fieldRepository;

    @InjectMocks
    private FieldService fieldService;

    private Field mockField;

    @BeforeEach
    void setUp() {
        mockField = mock(Field.class);
    }

    @Test
    public void testGetAppleCoordinates_ExistingGame() {
        List<Coordinates> expectedCoordinates = List.of(
                new Coordinates(1, 2),
                new Coordinates(3, 4)
        );
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        when(mockField.getAppleCoordinates()).thenReturn(expectedCoordinates);

        List<Coordinates> result = fieldService.getAppleCoordinates();

        assertEquals(expectedCoordinates, result);
        verify(fieldRepository).findByInGameTrue();
        verify(mockField).getAppleCoordinates();
    }

    @Test
    public void testGetAppleCoordinates_NewGame() {
        List<Coordinates> expectedCoordinates = List.of(new Coordinates(5, 5));
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.empty());
        when(mockField.getAppleCoordinates()).thenReturn(expectedCoordinates);


        try (var mockedField = mockStatic(Field.class)) {
            mockedField.when(() -> Field.createNewGame(20)).thenReturn(mockField);

            List<Coordinates> result = fieldService.getAppleCoordinates();

            assertEquals(expectedCoordinates, result);
            verify(fieldRepository).findByInGameTrue();
            verify(fieldRepository).save(mockField);
            verify(mockField).getAppleCoordinates();
        }
    }

    @Test
    public void testGetSnakeCoordinates_ExistingGame() {
        List<Coordinates> expectedCoordinates = List.of(
                new Coordinates(10, 10),
                new Coordinates(10, 11)
        );
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        when(mockField.getSnakeCoordinates()).thenReturn(expectedCoordinates);

        List<Coordinates> result = fieldService.getSnakeCoordinates();

        assertEquals(expectedCoordinates, result);
        verify(fieldRepository).findByInGameTrue();
        verify(mockField).getSnakeCoordinates();
    }

    @Test
    public void testGetSnakeCoordinates_NewGame() {
        List<Coordinates> expectedCoordinates = List.of(new Coordinates(10, 10));
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.empty());
        when(mockField.getSnakeCoordinates()).thenReturn(expectedCoordinates);

        try (var mockedField = mockStatic(Field.class)) {
            mockedField.when(() -> Field.createNewGame(20)).thenReturn(mockField);

            List<Coordinates> result = fieldService.getSnakeCoordinates();

            assertEquals(expectedCoordinates, result);
            verify(fieldRepository).findByInGameTrue();
            verify(fieldRepository).save(mockField);
            verify(mockField).getSnakeCoordinates();
        }
    }

    @Test
    public void testMoveSnake_SuccessfulMove() {
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        doNothing().when(mockField).moveSnake();

        fieldService.moveSnake();

        verify(fieldRepository).findByInGameTrue();
        verify(mockField).moveSnake();
        verify(fieldRepository).save(mockField);
        verify(mockField, never()).endGame();
    }

    @Test
    public void testMoveSnake_WithException() {
        RuntimeException runtimeException = new RuntimeException("Game over");
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        doThrow(runtimeException).when(mockField).moveSnake();

        fieldService.moveSnake();

        verify(fieldRepository).findByInGameTrue();
        verify(mockField).moveSnake();
        verify(mockField).endGame();
        verify(fieldRepository).save(mockField);
    }

    @Test
    public void testGetSIZE_ExistingGame() {
        int expectedSize = 20;
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        when(mockField.getSize()).thenReturn(expectedSize);

        int result = fieldService.getSIZE();

        assertEquals(expectedSize, result);
        verify(fieldRepository).findByInGameTrue();
        verify(mockField).getSize();
    }

    @Test
    public void testGetSIZE_NewGame() {
        int expectedSize = 20;
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.empty());
        when(mockField.getSize()).thenReturn(expectedSize);

        try (var mockedField = mockStatic(Field.class)) {
            mockedField.when(() -> Field.createNewGame(20)).thenReturn(mockField);

            int result = fieldService.getSIZE();

            assertEquals(expectedSize, result);
            verify(fieldRepository).findByInGameTrue();
            verify(fieldRepository).save(mockField);
            verify(mockField).getSize();
        }
    }

    @Test
    public void testFindInGameField_ExistingGame() {
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));

        Field result = fieldService.findInGameField();

        assertEquals(mockField, result);
        verify(fieldRepository).findByInGameTrue();
        verify(fieldRepository, never()).save(any());
    }

    @Test
    public void testFindInGameField_NewGame() {
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.empty());

        try (var mockedField = mockStatic(Field.class)) {
            mockedField.when(() -> Field.createNewGame(20)).thenReturn(mockField);

            Field result = fieldService.findInGameField();

            assertEquals(mockField, result);
            verify(fieldRepository).findByInGameTrue();
            verify(fieldRepository).save(mockField);
        }
    }

    @Test
    public void testConstructor() {
        FieldRepository repository = mock(FieldRepository.class);
        FieldService service = new FieldService(repository);

        assertNotNull(service);
    }

    @Test
    public void testFindInGameField_AlwaysCreatesSize20() {
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.empty());

        try (var mockedField = mockStatic(Field.class)) {
            mockedField.when(() -> Field.createNewGame(20)).thenReturn(mockField);

            fieldService.findInGameField();

            mockedField.verify(() -> Field.createNewGame(20));
        }
    }

    @Test
    public void testMoveSnake_ExceptionHandling() {
        when(fieldRepository.findByInGameTrue()).thenReturn(Optional.of(mockField));
        doThrow(new RuntimeException("Border collision")).when(mockField).moveSnake();

        assertDoesNotThrow(() -> fieldService.moveSnake());

        verify(mockField).moveSnake();
        verify(mockField).endGame();
        verify(fieldRepository).save(mockField);
    }
}