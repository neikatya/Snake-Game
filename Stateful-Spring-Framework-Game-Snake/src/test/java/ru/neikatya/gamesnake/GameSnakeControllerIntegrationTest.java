package ru.neikatya.gamesnake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.service.FieldService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameSnakeControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testGetAppleCoordinates_ShouldReturnListOfCoordinates() {
        // Act
        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(
                baseUrl + "/getAppleCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 0);
    }

    @Test
    void testGetSnakeCoordinates_ShouldReturnListOfCoordinates() {
        // Act
        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    void testMoveSnake_ShouldMoveSnakeAndReturnOk() {
        // Arrange - получаем начальные координаты
        ResponseEntity<List<Coordinates>> initialResponse = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        List<Coordinates> initialCoordinates = initialResponse.getBody();

        // Act - двигаем змею
        ResponseEntity<Void> moveResponse = restTemplate.postForEntity(
                baseUrl + "/moveSnake",
                null,
                Void.class
        );

        // Assert - проверяем ответ
        assertEquals(HttpStatus.OK, moveResponse.getStatusCode());

        // Проверяем, что координаты изменились
        ResponseEntity<List<Coordinates>> newResponse = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        List<Coordinates> newCoordinates = newResponse.getBody();

        assertNotEquals(initialCoordinates, newCoordinates);
    }

    @Test
    void testTurnSnake_WithValidDirection_ShouldReturnOk() {
        //перед поворотом необходимо сперва сдвинуть змею
        Field inGameField = fieldService.findInGameField();
        inGameField.moveSnake();
        fieldRepository.save(inGameField);
        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/turnTo",
                Direction.UP,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testTurnSnake_WithInvalidDirection_ShouldReturnServerError() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/turnTo",
                Direction.LEFT,
                Void.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

       response = restTemplate.postForEntity(
                baseUrl + "/turnTo",
                Direction.RIGHT,
                Void.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetFieldSize_ShouldReturnFieldSize() {
        ResponseEntity<Integer> response = restTemplate.getForEntity(
                baseUrl + "/getSize",
                Integer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(20, response.getBody()); // Размер по умолчанию
    }

    @Test
    void testGameStateConsistency_AfterMultipleOperations() {
        ResponseEntity<List<Coordinates>> initialApples = restTemplate.exchange(
                baseUrl + "/getAppleCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        ResponseEntity<List<Coordinates>> initialSnake = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );

        restTemplate.postForEntity(baseUrl + "/turnTo", Direction.RIGHT, Void.class);
        restTemplate.postForEntity(baseUrl + "/moveSnake", null, Void.class);
        restTemplate.postForEntity(baseUrl + "/turnTo", Direction.LEFT, Void.class);
        restTemplate.postForEntity(baseUrl + "/moveSnake", null, Void.class);

        ResponseEntity<List<Coordinates>> finalSnake = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );

        assertNotEquals(initialSnake.getBody(), finalSnake.getBody());
    }

    @Test
    void testAllEndpoints_AreAccessible() {
        String[] getEndpoints = {
                "/getAppleCoordinates",
                "/getSnakeCoordinates",
                "/getSize"
        };

        for (String endpoint : getEndpoints) {
            ResponseEntity<?> response = restTemplate.getForEntity(baseUrl + endpoint, Object.class);
            assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                    "GET endpoint " + endpoint + " should be available");
        }

        String[] postEndpoints = {
                "/moveSnake",
                "/turnTo"
        };

        for (String endpoint : postEndpoints) {
            ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl + endpoint, null, Void.class);
            assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                    "POST endpoint " + endpoint + " should be available");
        }
    }

    @AfterEach
    void closeGame() {
        Field inGameField = fieldService.findInGameField();
        inGameField.endGame();
        fieldRepository.save(inGameField);
    }
}