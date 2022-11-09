package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    //получить список объектов
    // эндпоинт GET /films
    @Test
    void getAllFilms() {
        ResponseEntity<Film> createdEntity = filmController.create(new Film("a", "b", LocalDate.now().minusYears(1), 99));
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);

        List<Film> films = filmController.getAllFilms();
        assertEquals(films.get(0), createdEntity.getBody());
    }

    //сохранить в контроллере объект с валидными полями
    //эндпоинт POST /films
    @Test
    void createValid() {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с пустым названием
    //эндпоинт POST /films
    @Test
    void createEmptyName() {
        Film film = new Film("", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с длинным описанием > 200 символов
    //эндпоинт POST /films
    @Test
    void createLongDescription() {
        Film film = new Film("a", "b".repeat(201), LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с длинным описанием, но равным 200 символов
    //эндпоинт POST /films
    @Test
    void createDescription200Chars() {
        Film film = new Film("a", "b".repeat(200), LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с датой релиза раньше 28 декабря 1895 года
    //эндпоинт POST /films
    @Test
    void createReleaseDateBefore_28_12_1895() {
        Film film = new Film("a", "b", LocalDate.of(1895, 12,27), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с датой релиза 28 декабря 1895 года
    //эндпоинт POST /films
    @Test
    void createReleaseDateEquals_28_12_1895() {
        Film film = new Film("a", "b", LocalDate.of(1895, 12,28), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с отрицательной длительностью
    //эндпоинт POST /films
    @Test
    void createNegativeDuration() {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), -1);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере объект с нулевой длительностью
    //эндпоинт POST /films
    @Test
    void createZeroDuration() {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 0);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(film, createdEntity.getBody());
    }

    //пытаемся сохранить в контроллере null
    //эндпоинт POST /films
    @Test
    void createNullPointer() {
        ResponseEntity<Film> createdEntity = filmController.create(null);

        assertEquals(createdEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(createdEntity.getBody());
    }

    //обновить в контроллере объект с валидными полями
    //эндпоинт PUT /films
    @Test
    void updateValid() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setName("aa");
        film.setDescription("bb");
        film.setReleaseDate(LocalDate.now().minusYears(2));
        film.setDuration(98);

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, присвоить пустое название
    //эндпоинт PUT /films
    @Test
    void updateEmptyName() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setName("");

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить длинное описание > 200 символов
    //эндпоинт PUT /films
    @Test
    void updateLongDescription() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setDescription("b".repeat(201));

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить длинное описание, но равное 200 символов
    //эндпоинт PUT /films
    @Test
    void updateDescription200Chars() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setDescription("b".repeat(200));

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить дату релиза раньше 28 декабря 1895 года
    //эндпоинт PUT /films
    @Test
    void updateReleaseDateBefore_28_12_1895() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setReleaseDate(LocalDate.of(1895, 12,27));

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить дату релиза равной 28 декабря 1895 года
    //эндпоинт PUT /films
    @Test
    void updateReleaseDateEquals_28_12_1895() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setReleaseDate(LocalDate.of(1895, 12,28));

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить отрицательную длительность
    //эндпоинт PUT /films
    @Test
    void updateNegativeDuration() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setDuration(-10);

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся обновить в контроллере объект, установить нулевую длительность
    //эндпоинт PUT /films
    @Test
    void updateZeroDuration() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResponseEntity<Film> createdEntity = filmController.create(film);

        film.setId(1);
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);
        assertEquals(film, createdEntity.getBody());

        //обновить
        film.setDuration(0);

        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //пытаемся передать в контроллер null
    //эндпоинт PUT /films
    @Test
    void updateNullPointer() {
        ResponseEntity<Film> updatedEntity = filmController.update(null);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertNull(updatedEntity.getBody());
    }

    //пытаемся обновить несуществующий объект
    //эндпоинт PUT /films
    @Test
    void updateIfNotExists() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);

        film.setId(1);
        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.NOT_FOUND, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

    //обновление с неверным id
    //эндпоинт PUT /films
    @Test
    void updateWithWrongId() {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);

        film.setId(-1);
        ResponseEntity<Film> updatedEntity = filmController.update(film);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(film, updatedEntity.getBody());
    }

}