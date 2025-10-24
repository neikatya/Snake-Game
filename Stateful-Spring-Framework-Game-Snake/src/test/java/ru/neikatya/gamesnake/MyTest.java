package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.neikatya.gamesnake.entity.Field;
import ru.neikatya.gamesnake.service.FieldService;

public class MyTest extends  AbstractIntegrationTest{

    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private FieldService fieldService;

    @Test
    @Transactional
    public void test(){
        Field inGameField = fieldService.findInGameField();
        System.out.println(inGameField);
        inGameField = fieldService.findInGameField();
        System.out.println(inGameField);

    }
}
