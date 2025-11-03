package ru.neikatya.gamesnake.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tail {

    @Column(name = "length", nullable = false)
    private int length;

    public static Tail createTail() {
        int length = 0;
        return new Tail(length);
    }


    public void increase(int increase) {
        try {
            if (increase <= 0) {
                throw new IllegalArgumentException("Значение increase должно быть положительным: " + increase);
            }
            length += increase;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка увеличения длины: " + e.getMessage());
        }
    }
}
