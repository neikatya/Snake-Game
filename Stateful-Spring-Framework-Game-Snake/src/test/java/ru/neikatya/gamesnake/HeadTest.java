package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.neikatya.gamesnake.entity.Head;

import static org.junit.jupiter.api.Assertions.*;

class HeadTest {

    @Test
    void createHead_ShouldCreateHeadWithLeftDirection() {
        Head head = Head.createHead();

        assertNotNull(head);
        assertEquals(Direction.LEFT, head.getDirection());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void turnRight_FromAllDirections_ShouldChangeDirectionCorrectly(Direction initialDirection) {
        Head head = new Head(initialDirection);
        Direction expectedDirection = getExpectedDirectionAfterRightTurn(initialDirection);

        head.turnRight();
        assertEquals(expectedDirection, head.getDirection());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void turnLeft_FromAllDirections_ShouldChangeDirectionCorrectly(Direction initialDirection) {
        Head head = new Head(initialDirection);
        Direction expectedDirection = getExpectedDirectionAfterLeftTurn(initialDirection);

        head.turnLeft();
        assertEquals(expectedDirection, head.getDirection());
    }


    @Test
    void turnRightAndLeft_ShouldReturnToOriginalDirection() {
        Head head = new Head(Direction.RIGHT);
        Direction originalDirection = head.getDirection();

        head.turnRight();
        head.turnLeft();

        assertEquals(originalDirection, head.getDirection());
    }


    @Test
    void constructor_WithDifferentDirections_ShouldSetCorrectDirection() {
        assertAll(
                () -> assertEquals(Direction.UP, new Head(Direction.UP).getDirection()),
                () -> assertEquals(Direction.DOWN, new Head(Direction.DOWN).getDirection()),
                () -> assertEquals(Direction.LEFT, new Head(Direction.LEFT).getDirection()),
                () -> assertEquals(Direction.RIGHT, new Head(Direction.RIGHT).getDirection())
        );
    }

    private Direction getExpectedDirectionAfterRightTurn(Direction initialDirection) {
        return switch (initialDirection) {
            case LEFT -> Direction.UP;
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
        };
    }

    private Direction getExpectedDirectionAfterLeftTurn(Direction initialDirection) {
        return switch (initialDirection) {
            case LEFT -> Direction.DOWN;
            case UP -> Direction.LEFT;
            case RIGHT -> Direction.UP;
            case DOWN -> Direction.RIGHT;
        };
    }
}