package ru.neikatya.gamesnake.entity.snake;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neikatya.gamesnake.Direction;
import ru.neikatya.gamesnake.Vector;
import ru.neikatya.gamesnake.entity.apple.Apple;
import ru.neikatya.gamesnake.entity.Head;
import ru.neikatya.gamesnake.entity.Tail;

import java.util.UUID;

@Entity
@Table(name = "snakes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Snake {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private Head head;

    @Embedded
    private Tail tail;

    @Column(name = "step_length", nullable = false)
    private final int stepLength = 1;

    @Version
    private Long version; // Добавьте это

    public static Snake createSnake() {
        Head head = Head.createHead();
        Tail tail = Tail.createTail();
        UUID id = null;
        return new Snake(id, head, tail, null);
    }


    public Vector move() {
        Vector vector = null;
        if (head.getDirection() == Direction.RIGHT) {
            vector = new Vector(stepLength, 0);
        }
        if (head.getDirection() == Direction.LEFT) {
            vector = new Vector(-1 * stepLength, 0);
        }
        if (head.getDirection() == Direction.UP) {
            vector = new Vector(0, -1 * stepLength);
        }
        if (head.getDirection() == Direction.DOWN) {
            vector = new Vector(0, stepLength);
        }
        return vector;
    }

    public int getLength() {
        return tail.getLength() + 1;
    }

    public void eating(Apple apple) {
        tail.increase(apple.getSatiety());
    }

    public void turnTo(Direction direction) {
        Direction currDirection = head.getDirection();
        Side sideForHeadTurn = selectSideForHeadTurn(currDirection, direction);

        switch (sideForHeadTurn) {
            case RIGHT -> head.turnRight();
            case LEFT -> head.turnLeft();
        }
    }

    private Side selectSideForHeadTurn(Direction from, Direction to) {
        int distanceBetweenDirectionsLeft = modN(to.getIndex() - from.getIndex(), 4);
        int distanceBetweenDirectionsRight = modN(from.getIndex() - to.getIndex(), 4);
        if (distanceBetweenDirectionsLeft % 2 == 0) {
            throw new IllegalStateException();
        }
        if (distanceBetweenDirectionsLeft <= distanceBetweenDirectionsRight) {
            return Side.RIGHT;
        } else {
            return Side.LEFT;
        }
    }

    private int modN(int number, int n) {
        while (number < 0) number += n;
        return number % n;
    }

    private enum Side {
        LEFT,
        RIGHT
    }

}

