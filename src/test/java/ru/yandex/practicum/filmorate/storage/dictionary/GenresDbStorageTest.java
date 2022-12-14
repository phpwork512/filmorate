package ru.yandex.practicum.filmorate.storage.dictionary;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresDbStorageTest {

    private final GenresDbStorage genresDbStorage;

    /**
     * Запросим запись ( 5, 'Документальный' ),
     */
    @Test
    void getById() {
        Genre genre = genresDbStorage.getById(5);
        assertEquals(new Genre(5, "Документальный"), genre);
    }

    /**
     * запросим все записи справочника
     */
    @Test
    void getAll() {
        List<Genre> genres = genresDbStorage.getAll();

        assertEquals(6, genres.size());
        assertEquals(new Genre(5, "Документальный"), genres.get(4));
    }
}