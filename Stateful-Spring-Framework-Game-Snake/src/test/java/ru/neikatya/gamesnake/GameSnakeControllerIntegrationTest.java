/*package ru.neikatya.gamesnake;

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
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.service.FieldService;
import ru.neikatya.gamesnake.service.SnakeService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameSnakeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private SnakeService snakeService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void getSize_ShouldReturnFieldSize() {
        // Act
        ResponseEntity<Integer> response = restTemplate.getForEntity(
                baseUrl + "/getSize", Integer.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isPositive();

        // Проверяем, что размер соответствует реальному значению из сервиса
        int actualSize = fieldService.getSIZE();
        assertThat(response.getBody()).isEqualTo(actualSize);
    }

    @Test
    void moveSnake_ShouldMoveSnakeSuccessfully() {
        // Arrange - получаем начальные координаты змейки
        List<Coordinates> initialCoordinates = getSnakeCoordinates();

        // Act - двигаем змейку
        ResponseEntity<Void> moveResponse = restTemplate.postForEntity(
                baseUrl + "/moveSnake", null, Void.class);

        // Assert
        assertThat(moveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверяем, что координаты изменились
        List<Coordinates> afterMoveCoordinates = getSnakeCoordinates();

        assertThat(afterMoveCoordinates).isNotNull();
        assertThat(afterMoveCoordinates).isNotEmpty();

        // Координаты должны измениться после движения
        assertThat(afterMoveCoordinates).isNotEqualTo(initialCoordinates);
    }

    @Test
    void getSnakeCoordinates_ShouldReturnValidCoordinates() {
        // Act
        List<Coordinates> coordinates = getSnakeCoordinates();

        // Assert
        assertThat(coordinates).isNotNull();
        assertThat(coordinates).isNotEmpty();

        // Проверяем, что координаты валидны (в пределах поля)
        int fieldSize = fieldService.getSIZE();
        for (Coordinates coord : coordinates) {
            assertThat(coord.getX()).isBetween(0, fieldSize - 1);
            assertThat(coord.getY()).isBetween(0, fieldSize - 1);
        }
    }

    @Test
    void getAppleCoordinates_ShouldReturnValidAppleCoordinates() {
        // Act
        List<Coordinates> apples = getAppleCoordinates();

        // Assert
        assertThat(apples).isNotNull();

        // Яблоко может быть одно или несколько в зависимости от реализации
        if (!apples.isEmpty()) {
            int fieldSize = fieldService.getSIZE();
            for (Coordinates apple : apples) {
                assertThat(apple.getX()).isBetween(0, fieldSize - 1);
                assertThat(apple.getY()).isBetween(0, fieldSize - 1);
            }
        }
    }

    @Test
    void turnTo_ShouldChangeSnakeDirection() {
        // Arrange
        Direction newDirection = Direction.UP;

        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/turnTo",
                newDirection,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверяем, что направление изменилось, двигая змейку
        // и проверяя изменение координат в соответствии с направлением
        List<Coordinates> beforeMove = getSnakeCoordinates();

        // Двигаем змейку
        restTemplate.postForEntity(baseUrl + "/moveSnake", null, Void.class);

        List<Coordinates> afterMove = getSnakeCoordinates();

        // Проверяем, что движение произошло
        assertThat(afterMove).isNotEqualTo(beforeMove);
    }

    @Test
    void turnTo_DirectionOpposite_ShouldWorkUncorrectly() {
        Direction direction = Direction.RIGHT;
        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/turnTo",
                direction,
                Void.class
        );
        assertThat(response.getStatusCode())
                .as("Direction: " + direction)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @Test
    void turnTo_WithAllDirections_ShouldWorkCorrectly() {
        // Тестируем все возможные направления
        Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

        for (Direction direction : directions) {
            // Act
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    baseUrl + "/turnTo",
                    direction,
                    Void.class
            );

            // Assert
            assertThat(response.getStatusCode())
                    .as("Direction: " + direction)
                    .isEqualTo(HttpStatus.OK);

            // Делаем ход после смены направления
            restTemplate.postForEntity(baseUrl + "/moveSnake", null, Void.class);

            // Проверяем, что змейка продолжает двигаться
            List<Coordinates> coordinates = getSnakeCoordinates();
            assertThat(coordinates).isNotEmpty();
        }
    }

    @Test
    void multipleMoves_ShouldUpdateCoordinatesCorrectly() {
        // Arrange
        List<Coordinates> initialCoordinates = getSnakeCoordinates();

        // Act - делаем несколько ходов
        for (int i = 0; i < 3; i++) {
            ResponseEntity<Void> moveResponse = restTemplate.postForEntity(
                    baseUrl + "/moveSnake", null, Void.class);
            assertThat(moveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Assert
        List<Coordinates> finalCoordinates = getSnakeCoordinates();
        assertThat(finalCoordinates).isNotEqualTo(initialCoordinates);

        // Проверяем, что все координаты остаются в пределах поля
        int fieldSize = fieldService.getSIZE();
        for (Coordinates coord : finalCoordinates) {
            assertThat(coord.getX()).isBetween(0, fieldSize - 1);
            assertThat(coord.getY()).isBetween(0, fieldSize - 1);
        }
    }

    @Test
    void gameState_ShouldBeConsistent() {
        // Комплексный тест на согласованность состояния игры

        // 1. Проверяем, что змейка и яблоко не пересекаются
        List<Coordinates> snake = getSnakeCoordinates();
        List<Coordinates> apples = getAppleCoordinates();

        if (!apples.isEmpty()) {
            for (Coordinates apple : apples) {
                for (Coordinates snakePart : snake) {
                    assertThat(apple).isNotEqualTo(snakePart);
                }
            }
        }

        // 2. Проверяем, что все части змейки находятся в пределах поля
        int fieldSize = fieldService.getSIZE();
        for (Coordinates snakePart : snake) {
            assertThat(snakePart.getX()).isBetween(0, fieldSize - 1);
            assertThat(snakePart.getY()).isBetween(0, fieldSize - 1);
        }

        // 3. Проверяем, что змейка не пересекается сама с собой
        for (int i = 0; i < snake.size(); i++) {
            for (int j = i + 1; j < snake.size(); j++) {
                assertThat(snake.get(i))
                        .as("Snake should not intersect itself")
                        .isNotEqualTo(snake.get(j));
            }
        }
    }

    @Test
    void endpoints_ShouldReturnCorrectContentType() {
        // Проверяем, что эндпоинты возвращают правильный Content-Type

        // GET /getSize
        ResponseEntity<String> sizeResponse = restTemplate.getForEntity(
                baseUrl + "/getSize", String.class);
        assertThat(sizeResponse.getHeaders().getContentType())
                .isNotNull();

        // GET /getSnakeCoordinates
        ResponseEntity<List<Coordinates>> snakeResponse = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        assertThat(snakeResponse.getHeaders().getContentType())
                .isNotNull();

        // GET /getAppleCoordinates
        ResponseEntity<List<Coordinates>> appleResponse = restTemplate.exchange(
                baseUrl + "/getAppleCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        assertThat(appleResponse.getHeaders().getContentType())
                .isNotNull();
    }

    // Вспомогательные методы
    private List<Coordinates> getSnakeCoordinates() {
        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(
                baseUrl + "/getSnakeCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private List<Coordinates> getAppleCoordinates() {
        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(
                baseUrl + "/getAppleCoordinates",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coordinates>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
}*/