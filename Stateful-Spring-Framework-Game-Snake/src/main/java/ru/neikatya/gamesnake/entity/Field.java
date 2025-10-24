package ru.neikatya.gamesnake.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.neikatya.gamesnake.Vector;
import ru.neikatya.gamesnake.entity.apple.AppleOnField;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.entity.coordinates.OrderedCoordinate;
import ru.neikatya.gamesnake.entity.snake.Snake;
import ru.neikatya.gamesnake.entity.snake.SnakeOnField;

import java.util.*;

@Entity
@Table(name = "fields")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "snake_on_field_id", referencedColumnName = "id")
    private SnakeOnField snakeOnField;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private List<AppleOnField> applesOnField;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "in_game", nullable = false)
    private boolean inGame;

    @Version
    private Long version; // Добавьте это


    // Фабричный метод для создания нового игрового поля
    public static Field createNewGame(int size) {
        Snake snake = Snake.createSnake();
        // Создаем начальные координаты для змеи
        List<Coordinates> initialSnakeCoordinates = new ArrayList<>();
        for (int i = 0; i < snake.getLength(); i++) {
            initialSnakeCoordinates.add(Coordinates.createCoordinates(size / 2, size / 2));
        }


        return new Field(
                null,
                SnakeOnField.createFrom(initialSnakeCoordinates, snake),
                List.of(AppleOnField.generateAppleOnField(size)),
                size,
                true,
                null
        );
    }


    public void moveSnake() {
        Vector move = snakeOnField.getSnake().move();
        Coordinates head = snakeOnField.getFirstCoordinates();
        Coordinates newHead = Coordinates.createCoordinates(head.getX() + move.x(), head.getY() + move.y());

        checkBorder(newHead);
        checkTail(newHead);

        // Обновляем координаты змеи
        snakeOnField.addFirstCoordinates(newHead);

        // Проверяем столкновение с яблоком
        Optional<AppleOnField> appleInNewCoordinates = applesOnField.stream()
                .filter(appleOnField -> appleOnField.getCoordinates().equals(newHead))
                .findFirst();

        if (appleInNewCoordinates.isPresent()) {
            snakeOnField.eatApple(appleInNewCoordinates.get().getApple());
            applesOnField.remove(appleInNewCoordinates.get());
            add(generateAppleOnField());
        }

        snakeOnField.syncLengthWithCoordinates();

    }

    public void checkBorder(Coordinates coordinates) {
        if (coordinates.getX() < 0 || coordinates.getY() < 0 ||
            coordinates.getX() >= size || coordinates.getY() >= size) {
            endGame();
            throw new RuntimeException("Выход за границу поля");
        }
    }

    public void checkTail(Coordinates coordinates) {
        if (snakeOnField.contains(coordinates)) {
            endGame();
            throw new RuntimeException("Врезание в хвост");
        }
    }

    public void add(AppleOnField appleOnField) {
        applesOnField.add(appleOnField);
    }

    public AppleOnField generateAppleOnField() {
        AppleOnField newAppleOnField;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            newAppleOnField = AppleOnField.generateAppleOnField(size);
            attempts++;
            if (attempts > maxAttempts) {
                throw new RuntimeException("Не удалось найти свободную позицию для яблока");
            }
        } while (alreadyExistsObjectOnFieldBy(newAppleOnField.getCoordinates()));

        return newAppleOnField;
    }

    private boolean alreadyExistsObjectOnFieldBy(Coordinates coordinates) {
        return
                snakeOnField.contains(coordinates) ||
                applesOnField
                        .stream()
                        .map(AppleOnField::getCoordinates)
                        .anyMatch(coordinate -> coordinate.equals(coordinates));
    }

    public List<Coordinates> getAppleCoordinates() {
        return applesOnField.stream()
                .map(AppleOnField::getCoordinates)
                .toList();
    }

    public List<Coordinates> getSnakeCoordinates() {
        return snakeOnField != null ?
                snakeOnField
                        .getOrderedCoordinates()
                        .stream()
                        .map(OrderedCoordinate::getCoordinate)
                        .toList() :
                new ArrayList<>();
    }

    public void endGame() {
        this.inGame = false;
    }

    public Snake getSnake() {
        return snakeOnField.getSnake();
    }
}
