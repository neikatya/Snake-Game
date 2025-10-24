package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.neikatya.gamesnake.entity.Tail;

import static org.junit.jupiter.api.Assertions.*;

class TailTest {

    @Test
    void increase_WithPositiveValue_ShouldIncreaseLength() {
        Tail tail = new Tail(3);
        int increaseValue = 2;
        int expectedLength = 5;

        tail.increase(increaseValue);
        assertEquals(expectedLength, tail.getLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5, -100})
    void increase_WithNonPositiveValue_ShouldHandleException(int invalidIncrease) {
        Tail tail = new Tail(5);
        int initialLength = tail.getLength();

        tail.increase(invalidIncrease);
        assertEquals(initialLength, tail.getLength());
    }

    @Test
    void increase_WithBoundaryValues() {
        Tail tail = new Tail(Integer.MAX_VALUE - 5);

        assertDoesNotThrow(() -> tail.increase(5));
        assertEquals(Integer.MAX_VALUE, tail.getLength());
    }
}