package ru.neikatya.gamesnake.entity.snake;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neikatya.gamesnake.entity.apple.Apple;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.entity.coordinates.OrderedCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "snakes_on_field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnakeOnField {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "snake_id", referencedColumnName = "id")
    private Snake snake;

    @ElementCollection
    @CollectionTable(
            name = "snake_coordinates",
            joinColumns = @JoinColumn(name = "snake_on_field_id")
    )
    @OrderBy("order DESC")
    private List<OrderedCoordinate> orderedCoordinates = new ArrayList<>();

    @Version
    private Long version;

    public static SnakeOnField createFrom(List<Coordinates> coordinates, Snake snake) {
        return new SnakeOnField(
                null,
                snake,
                new ArrayList<>(coordinates
                        .stream()
                        .map(coordinate -> OrderedCoordinate.createNew(coordinate, 0))
                        .toList()),
                null
        );

    }

    public Coordinates getFirstCoordinates() {
        return orderedCoordinates.getLast().getCoordinate();

    }

    public void eatApple(Apple apple) {
        snake.eating(apple);
    }

    public void syncLengthWithCoordinates() {
        while (snake.getLength() < orderedCoordinates.size()) {
            orderedCoordinates.removeFirst();
        }
    }

    public void addFirstCoordinates(Coordinates coordinates) {
        orderedCoordinates
                .addLast(
                        OrderedCoordinate.createNew(
                                coordinates, orderedCoordinates.getFirst().getOrder() - 1
                        )
                );
    }

    public boolean contains(Coordinates coordinates) {
        return orderedCoordinates.stream().anyMatch(
                orderedCoordinate -> orderedCoordinate.getCoordinate().equals(coordinates)
        );
    }
}