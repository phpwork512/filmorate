package ru.yandex.practicum.filmorate.storage.dictionary;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRatingDbStorageTest {

    private final MpaRatingDbStorage mpaRatingDbStorage;

    /**
     * Запросим запись ( 5, 'NC-17' )
     */
    @Test
    void getById() {
        MpaRating mpaRating = mpaRatingDbStorage.getById(5);
        assertEquals(new MpaRating(5, "NC-17"), mpaRating);
    }

    /**
     * запросим все записи справочника
     */
    @Test
    void getAll() {
        List<MpaRating> mpaRatings = mpaRatingDbStorage.getAll();

        assertEquals(5, mpaRatings.size());
        assertEquals(new MpaRating(5, "NC-17"), mpaRatings.get(4));
    }
}