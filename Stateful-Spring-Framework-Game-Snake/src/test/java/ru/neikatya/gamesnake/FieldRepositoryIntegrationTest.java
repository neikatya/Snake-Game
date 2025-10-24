package ru.neikatya.gamesnake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.neikatya.gamesnake.entity.Field;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FieldRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FieldRepository fieldRepository;

    @Test
    void contextLoads() {
        assertNotNull(entityManager);
        assertNotNull(fieldRepository);
    }

    @Test
    void testSaveAndFindField() {
        Field field = new Field();
        field.setSize(20);
        field.setInGame(true);

        Field savedField = fieldRepository.save(field);
        Optional<Field> foundField = fieldRepository.findById(savedField.getId());

        assertTrue(foundField.isPresent());
        assertEquals(20, foundField.get().getSize());
        assertTrue(foundField.get().isInGame());
    }

    @Test
    void testFindByInGameTrue() {
        Field activeField = new Field();
        activeField.setSize(20);
        activeField.setInGame(true);
        fieldRepository.save(activeField);

        Field inactiveField = new Field();
        inactiveField.setSize(15);
        inactiveField.setInGame(false);
        fieldRepository.save(inactiveField);

        Optional<Field> result = fieldRepository.findByInGameTrue();

        assertTrue(result.isPresent());
        assertTrue(result.get().isInGame());
    }

    @Test
    void testFindBySize() {
        Field field1 = new Field();
        field1.setSize(20);
        field1.setInGame(true);
        fieldRepository.save(field1);

        Field field2 = new Field();
        field2.setSize(20);
        field2.setInGame(false);
        fieldRepository.save(field2);

        List<Field> result = fieldRepository.findBySize(20);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(f -> f.getSize() == 20));
    }
}