package ru.neikatya.gamesnake.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neikatya.gamesnake.Direction;
import ru.neikatya.gamesnake.FieldRepository;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.entity.snake.Snake;

@AllArgsConstructor
@Service
public class SnakeService {
    private final FieldRepository fieldRepository;
    private final FieldService fieldService;

    public void turnTo(Direction direction){
        Field inGameField = fieldService.findInGameField();
        inGameField.getSnake().turnTo(direction);
        fieldRepository.save(inGameField);
    }

    public Snake getSnake() {
        return fieldService.findInGameField().getSnake();
    }
}
