package ru.neikatya.gamesnake.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.neikatya.gamesnake.FieldRepository;
import ru.neikatya.gamesnake.entity.coordinates.Coordinates;
import ru.neikatya.gamesnake.entity.Field;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class FieldService {
    private final FieldRepository fieldRepository;

    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public List<Coordinates> getAppleCoordinates() {
        return   findInGameField().getAppleCoordinates();
    }

    public List<Coordinates> getSnakeCoordinates() {
        return findInGameField().getSnakeCoordinates();
    }

    public void moveSnake() {
        Field inGameField = findInGameField();
        try{
            inGameField.moveSnake();
            fieldRepository.save(inGameField);
        }
        catch (RuntimeException e){
            inGameField.endGame();
            fieldRepository.save(inGameField);
        }
    }

    public int getSIZE(){
        return findInGameField().getSize();
    }

    public Field findInGameField() {
        Optional<Field> alreadyInGameField = fieldRepository.findByInGameTrue();
        if (alreadyInGameField.isPresent()) {
            return alreadyInGameField.get();
        } else {
            Field newGame = Field.createNewGame(20);
            fieldRepository.save(newGame);
            return newGame;
        }
    }

}



