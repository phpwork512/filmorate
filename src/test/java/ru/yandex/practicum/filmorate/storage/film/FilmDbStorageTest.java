package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static boolean dbIsInitialized = false;

    /**
     * начальная инициализация базы для тестов
     */
    @BeforeEach
    public void setUpUserRelatedTables() {
        if (!dbIsInitialized) { //нужно заполнить таблицы только один раз сразу для всех тестов тут
            dbIsInitialized = true;

            filmStorage.clearAll();
            userStorage.clearAll();

            //запись фильм id=1
            jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)"
                    , "Name-1"
                    , "Test description 1"
                    , LocalDate.of(2022, 12, 12).toString()
                    , 90
                    , 1);

            //запись фильм id=2
            jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)"
                    , "Name-2"
                    , "Test description 2"
                    , LocalDate.of(2021, 1, 1).toString()
                    , 91
                    , 2);

            //записать жанр 1-Комедия в фильм 1
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)"
                    , 1
                    , 1);

            //вставить лайк фильму 1 от пользователя 1
            //вставить пользователя
            jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)"
                    , "aa@mm.ru"
                    , "testlogin"
                    , "Name"
                    , LocalDate.of(2012, 12, 12).toString());

            //вставить лайк
            jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)"
                    , 1
                    , 1);


        }
    }

    /**
     * тест получения записи по id
     */
    @Test
    void getById() {
        Film film = filmStorage.getById(1);
        assertNotNull(film);
        assertEquals("Name-1", film.getName());
        assertEquals("Test description 1", film.getDescription());
        assertEquals(LocalDate.of(2022, 12, 12), film.getReleaseDate());
        assertEquals(90, film.getDuration());
        assertEquals(new MpaRating(1, "G"), film.getMpa());
        assertEquals(new Genre(1, "Комедия"), film.getGenres().get(0));
    }

    /**
     * тест получения записи по несуществующему id
     */
    @Test
    void getByIdIfNoSuchRecord() {
        Film film = filmStorage.getById(100);
        assertNull(film);
    }

    /**
     * тест получения всех записей
     */
    @Test
    void getAll() {
        List<Film> films = filmStorage.getAll();
        assertNotNull(films);
        assertEquals(3, films.size());

        assertEquals(1, films.get(0).getId());
        assertEquals("Name-1", films.get(0).getName());
        assertEquals(new Genre(1, "Комедия"), films.get(0).getGenres().get(0));
        assertEquals(2, films.get(1).getId());
        assertEquals("Name-2", films.get(1).getName());
    }

    /**
     * тест создания записи
     */
    @Test
    void create() {
        Film film = new Film("Name-3"
                , "Test description 3"
                , LocalDate.of(2020, 10, 10)
                , 121
                , new MpaRating(4)
                , List.of(new Genre(1), new Genre(3)));

        Film createdFilm = filmStorage.create(film);
        assertNotNull(createdFilm);
        assertEquals("Name-3", createdFilm.getName());
        assertEquals(new Genre(1, "Комедия"), createdFilm.getGenres().get(0));
        assertEquals(new Genre(3, "Мультфильм"), createdFilm.getGenres().get(1));
    }

    /**
     * тест обновления записи
     */
    @Test
    void update() {
        Film film = filmStorage.getById(3);
        assertNotNull(film);

        film.getGenres().add(new Genre(6));
        film.setName("Name-3-1");
        film.setMpa(new MpaRating(5));

        Film updatedFilm = filmStorage.update(film);
        assertNotNull(updatedFilm);
        assertEquals("Name-3-1", updatedFilm.getName());
        assertEquals(new MpaRating(5, "NC-17"), updatedFilm.getMpa());
        assertEquals(3, updatedFilm.getGenres().size());
        assertEquals(new Genre(1, "Комедия"), updatedFilm.getGenres().get(0));
        assertEquals(new Genre(3, "Мультфильм"), updatedFilm.getGenres().get(1));
        assertEquals(new Genre(6, "Боевик"), updatedFilm.getGenres().get(2));
    }

    /**
     * обновить запись установив один и тот же жанр 2 раза
     */
    @Test
    void updateWithGenreTwice() {
        Film film = filmStorage.getById(2);
        assertNotNull(film);

        film.getGenres().add(new Genre(6));
        film.getGenres().add(new Genre(3));
        film.getGenres().add(new Genre(6));

        Film updatedFilm = filmStorage.update(film);
        assertNotNull(updatedFilm);
        assertEquals(2, updatedFilm.getGenres().size());
        assertEquals(new Genre(3, "Мультфильм"), updatedFilm.getGenres().get(0));
        assertEquals(new Genre(6, "Боевик"), updatedFilm.getGenres().get(1));
    }

    /**
     * тест добавления лайка
     */
    @Test
    void addLike() {
        Film film = filmStorage.getById(2);
        assertNotNull(film);

        filmStorage.addLike(film, 1);

        film = filmStorage.getById(2);
        assertNotNull(film);
        assertEquals(1, film.getLikedUserIdSet().size());
    }

    /**
     * тест удаления лайка
     */
    @Test
    void removeLike() {
        Film film = filmStorage.getById(2);
        assertNotNull(film);
        filmStorage.removeLike(film, 1);

        film = filmStorage.getById(2);
        assertNotNull(film);
        assertEquals(0, film.getLikedUserIdSet().size());
    }

    /**
     * тест получения списка популярных фильмов
     */
    @Test
    void getPopularFilms() {
        List<Film> popularFilms = filmStorage.getPopularFilms(0);
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.size());
        assertEquals(1, popularFilms.get(0).getId());
        assertEquals(2, popularFilms.get(1).getId());
    }
}