package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    public void testDirectionValues() {
        Direction[] directions = Direction.values();
        assertEquals(4, directions.length);
        assertArrayEquals(new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT}, directions);
    }

    @Test
    public void testDirectionIndexes() {
        assertEquals(0, Direction.UP.getIndex());
        assertEquals(1, Direction.RIGHT.getIndex());
        assertEquals(2, Direction.DOWN.getIndex());
        assertEquals(3, Direction.LEFT.getIndex());
    }

    @Test
    public void testValueOf() {
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
    }
}