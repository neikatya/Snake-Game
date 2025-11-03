package ru.neikatya.gamesnake.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neikatya.gamesnake.Direction;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Head {

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private Direction direction;

    public static Head createHead() {
        Direction direction = Direction.LEFT;
        return new Head(direction);
    }

    public void turnRight() {
        switch (direction) {
            case LEFT -> direction = Direction.UP;
            case UP -> direction = Direction.RIGHT;
            case RIGHT -> direction = Direction.DOWN;
            case DOWN -> direction = Direction.LEFT;
        }
    }

    public void turnLeft() {
        switch (direction) {
            case LEFT -> direction = Direction.DOWN;
            case UP -> direction = Direction.LEFT;
            case RIGHT -> direction = Direction.UP;
            case DOWN -> direction = Direction.RIGHT;
        }
    }

}


