package ru.neikatya.gamesnake.entity.apple;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;

import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "apples_on_field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppleOnField {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private Coordinates coordinates;

    @Embedded
    private Apple apple;

    @Version
    private Long version;

    public static AppleOnField createAppleOnField(Coordinates coordinates) {
        Apple apple = Apple.createApple();
        return new AppleOnField(null, coordinates, apple, null);
    }

    public static AppleOnField generateAppleOnField(int fieldSize) {
        Random random = new Random();
        Apple newApple = Apple.createApple();
        Coordinates newAppleCoordinates = Coordinates.createCoordinates(random.nextInt(fieldSize), random.nextInt(fieldSize));
        return new AppleOnField(null, newAppleCoordinates, newApple, null);
    }

}

