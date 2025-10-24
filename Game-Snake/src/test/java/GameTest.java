import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameTest {

    @Mock private GameSnakeClient mockGameClient;
    @Mock private Graphics mockGraphics;

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game(mockGameClient);
    }

    @Test
    public void testConstructorWithBaseUrl() {
        String testUrl = "http://test-server:8080";
        Game gameWithUrl = new Game(testUrl);
        assertNotNull(gameWithUrl);
    }

    @Test
    public void testActionPerformedInGame() {
        when(mockGameClient.isServerAvailable()).thenReturn(true);
        game = new Game(mockGameClient);

        ActionEvent mockEvent = mock(ActionEvent.class);
        game.actionPerformed(mockEvent);

        verify(mockGameClient).moveSnake();
    }

    @Test
    public void testActionPerformedGameOver() {
        game.endGame();
        ActionEvent mockEvent = mock(ActionEvent.class);
        game.actionPerformed(mockEvent);

        verify(mockGameClient, never()).moveSnake();
    }

    @Test
    public void testActionPerformedWithException() {
        when(mockGameClient.isServerAvailable()).thenReturn(true);
        game = new Game(mockGameClient);

        ActionEvent mockEvent = mock(ActionEvent.class);
        doThrow(new RuntimeException("Game Over")).when(mockGameClient).moveSnake();
        game.actionPerformed(mockEvent);

        assertFalse(game.getStateOfGame());
    }

    @Test
    public void testActionPerformedWithNetworkException() {
        when(mockGameClient.isServerAvailable()).thenReturn(true);
        game = new Game(mockGameClient);

        ActionEvent mockEvent = mock(ActionEvent.class);
        doThrow(new RuntimeException("Network error")).when(mockGameClient).moveSnake();
        game.actionPerformed(mockEvent);

        assertFalse(game.getStateOfGame());
    }

    @Test
    public void testKeyListenerLeft() {
        Game.FieldKeyListener listener = game.new FieldKeyListener();
        KeyEvent mockEvent = mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_LEFT);

        listener.keyPressed(mockEvent);

        verify(mockGameClient).turnTo(Direction.LEFT);
    }

    @Test
    public void testKeyListenerRight() {
        Game.FieldKeyListener listener = game.new FieldKeyListener();
        KeyEvent mockEvent = mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_RIGHT);

        listener.keyPressed(mockEvent);

        verify(mockGameClient).turnTo(Direction.RIGHT);
    }

    @Test
    public void testKeyListenerUp() {
        Game.FieldKeyListener listener = game.new FieldKeyListener();
        KeyEvent mockEvent = mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_UP);

        listener.keyPressed(mockEvent);

        verify(mockGameClient).turnTo(Direction.UP);
    }

    @Test
    public void testKeyListenerDown() {
        Game.FieldKeyListener listener = game.new FieldKeyListener();
        KeyEvent mockEvent = mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_DOWN);

        listener.keyPressed(mockEvent);

        verify(mockGameClient).turnTo(Direction.DOWN);
    }

    @Test
    public void testKeyListenerOtherKey() {
        Game.FieldKeyListener listener = game.new FieldKeyListener();
        KeyEvent mockEvent = mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_SPACE);

        listener.keyPressed(mockEvent);

        verify(mockGameClient, never()).turnTo(any());
    }

    @Test
    public void testPaintComponentInGame() {
        List<Coordinates> appleCoords = Arrays.asList(new Coordinates(2, 3));
        List<Coordinates> snakeCoords = Arrays.asList(new Coordinates(1, 1), new Coordinates(1, 2));

        when(mockGameClient.getAppleCoordinates()).thenReturn(appleCoords);
        when(mockGameClient.getSnakeCoordinates()).thenReturn(snakeCoords);
        when(mockGameClient.getSize()).thenReturn(20);

        game.paintComponent(mockGraphics);

        verify(mockGraphics, atLeast(2)).drawImage(any(Image.class), anyInt(), anyInt(), any());
    }

    @Test
    public void testPaintComponentGameOver() {
        game.endGame();
        when(mockGameClient.getSize()).thenReturn(20);

        game.paintComponent(mockGraphics);

        verify(mockGraphics).drawString(eq("Game Over"), eq(125), eq(160)); // 20 * 16 / 2 = 160
    }

    @Test
    public void testPaintComponentWithNetworkError() {
        when(mockGameClient.getAppleCoordinates()).thenThrow(new RuntimeException("Network error"));
        when(mockGameClient.getSnakeCoordinates()).thenThrow(new RuntimeException("Network error"));
        when(mockGameClient.getSize()).thenReturn(20);

        game.paintComponent(mockGraphics);

        assertFalse(game.getStateOfGame());
    }

    @Test
    public void testEndGame() {
        assertTrue(game.getStateOfGame());
        game.endGame();
        assertFalse(game.getStateOfGame());
    }

    @Test
    public void testIsServerAvailable() {
        when(mockGameClient.isServerAvailable()).thenReturn(true);
        assertTrue(game.isServerAvailable());

        when(mockGameClient.isServerAvailable()).thenReturn(false);
        assertFalse(game.isServerAvailable());
    }

    @Test
    public void testLoadImages() {

        assertDoesNotThrow(() -> game.loadImages());
    }

}

