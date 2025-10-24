package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import ru.neikatya.gamesnake.entity.apple.Apple;

import static org.junit.jupiter.api.Assertions.*;

class AppleTest {

    @Test
    public void testCreateApple() {
        Apple apple = Apple.createApple();
        assertNotNull(apple);
        assertTrue(apple.getSatiety() >= 1 && apple.getSatiety() <= 3);
    }
}