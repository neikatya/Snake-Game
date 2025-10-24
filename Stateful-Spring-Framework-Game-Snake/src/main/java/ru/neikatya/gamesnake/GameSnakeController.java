package ru.neikatya.gamesnake;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.service.FieldService;
import ru.neikatya.gamesnake.service.SnakeService;

import java.util.List;

@RestController
public class GameSnakeController {
    private final FieldService fieldService;
    private final SnakeService snakeService;

    public GameSnakeController(
            FieldService fieldService,
            SnakeService snakeService
    ) {
        this.fieldService = fieldService;
        this.snakeService = snakeService;
    }

    @GetMapping("/getSize")
    public int getSize() {
        return fieldService.getSIZE();
    }
    @PostMapping("/moveSnake")
    public void moveSnake() {
        fieldService.moveSnake();
    }
    @GetMapping("/getSnakeCoordinates")
    public List<Coordinates> getSnakeCoordinates() {
        return fieldService.getSnakeCoordinates();
    }
    @GetMapping("/getAppleCoordinates")
    public List<Coordinates> getAppleCoordinates() {
        return fieldService.getAppleCoordinates();
    }

    @PostMapping("/turnTo")
    public void turnTo(@RequestBody Direction direction) {
        snakeService.turnTo(direction);
    }

}
