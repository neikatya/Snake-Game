package ru.neikatya.gamesnake.entity.apple;

import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apple {

    @Column(name = "satiety", nullable = false)
    private int satiety;

    public static Apple createApple() {
        int randomSatiety = new Random().nextInt(3) + 1;
        return new Apple(randomSatiety);
    }

}