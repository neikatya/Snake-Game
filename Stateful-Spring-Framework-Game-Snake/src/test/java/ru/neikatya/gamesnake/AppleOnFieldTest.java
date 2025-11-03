package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import ru.neikatya.gamesnake.entity.apple.AppleOnField;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;


import static org.junit.jupiter.api.Assertions.*;

class AppleOnFieldTest {

    @Test
    public void testCreateAppleOnField() {
        Coordinates coordinates = new Coordinates(3, 4);

        AppleOnField result = AppleOnField.createAppleOnField(coordinates);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(coordinates, result.getCoordinates());
        assertNotNull(result.getApple());
        assertNull(result.getVersion());
    }

    @Test
    public void testGenerateAppleOnField() {
        int fieldSize = 10;

        AppleOnField result = AppleOnField.generateAppleOnField(fieldSize);

        assertNotNull(result);
        assertNull(result.getId());
        assertNotNull(result.getCoordinates());
        assertNotNull(result.getApple());
        assertNull(result.getVersion());

        assertTrue(result.getCoordinates().getX() >= 0 && result.getCoordinates().getX() < fieldSize);
        assertTrue(result.getCoordinates().getY() >= 0 && result.getCoordinates().getY() < fieldSize);
    }

    @RepeatedTest(10)
    public void testGenerateAppleOnField_RandomCoordinates() {
        int fieldSize = 5;

        AppleOnField result = AppleOnField.generateAppleOnField(fieldSize);

        assertTrue(result.getCoordinates().getX() >= 0 && result.getCoordinates().getX() < fieldSize);
        assertTrue(result.getCoordinates().getY() >= 0 && result.getCoordinates().getY() < fieldSize);

        assertTrue(result.getApple().getSatiety() >= 1 && result.getApple().getSatiety() <= 3);
    }
}