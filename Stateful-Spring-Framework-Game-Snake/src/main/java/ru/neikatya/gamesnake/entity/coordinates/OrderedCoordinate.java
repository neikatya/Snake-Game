package ru.neikatya.gamesnake.entity.coordinates;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neikatya.gamesnake.entity.snake.SnakeOnField;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderedCoordinate {

    @Embedded
    private Coordinates coordinate;

    @Column(name = "coordinate_order")
    private Integer order;

    public static OrderedCoordinate createNew(Coordinates coordinates, int order) {
        return new OrderedCoordinate(coordinates, order);
    }
}