package ru.neikatya.gamesnake.entity.coordinates;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    @Column(name = "x_coordinate", nullable = false)
    private int x;

    @Column(name = "y_coordinate", nullable = false)
    private int y;
    public static Coordinates createCoordinates(int x, int y) {
        return new Coordinates(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

}
