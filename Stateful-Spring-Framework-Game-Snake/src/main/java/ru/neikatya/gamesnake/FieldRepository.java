package ru.neikatya.gamesnake;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neikatya.gamesnake.entity.Field;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FieldRepository extends JpaRepository<Field, UUID> {
    Optional<Field> findByInGameTrue();

    boolean existsByInGameTrue();

    List<Field> findBySize(int size);

    List<Field> findByInGameFalse();

    long countByInGameTrue();

}