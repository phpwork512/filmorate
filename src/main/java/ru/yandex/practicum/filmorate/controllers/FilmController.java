package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ParameterValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.ErrorResponse;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    /**
     * вернуть данные по всем фильмам
     *
     * @return список объектов типа Film
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAll();
    }

    /**
     * вернуть данные фильма с указанным id
     *
     * @param filmId id фильма
     * @return объект типа Film, статус OK если всё хорошо
     */
    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable int filmId) {
        Film film = filmService.getById(filmId);
        if (film != null) {
            return film;
        } else {
            throw new FilmNotFoundException("Фильм с указанным id не найден: " + filmId);
        }
    }

    /**
     * создает фильм, проверяет валидность полученных данных, присваивает уникальный id
     *
     * @param film заполненный данными объект класса Film
     * @return объект класса Film, статус CREATED если всё ок
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        log.info(film.toString());
        FilmValidator.validate(film);

        return filmService.create(film);
    }

    /**
     * обновляет фильм, проверяет валидность полученных данных
     *
     * @param film заполненный данными объект класса Film
     * @return заполненный данными объект класса Film, статус OK если всё ок
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@RequestBody Film film) {
        log.info(film.toString());
        FilmValidator.validate(film);

        Film updatedFilm = filmService.update(film);

        if (updatedFilm != null) {
            return updatedFilm;
        } else {
            throw new FilmNotFoundException("Фильм с указанным id не найден: " + film.getId());
        }
    }

    /**
     * добавить лайк фильму от пользователя
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeToFilm(@PathVariable int filmId, @PathVariable int userId) {
        filmService.likeFilmById(filmId, userId);
    }

    /**
     * убрать лайк фильму от пользователя
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLikeToFilm(@PathVariable int filmId, @PathVariable int userId) {
        filmService.dislikeFilmById(filmId, userId);
    }

    /**
     * вернуть список из N наиболее популярных фильмов по лайкам
     *
     * @param count количество фильмов в списке, если не указано то берется 10
     * @return список объектов типа Film
     */
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(@RequestParam(name = "count", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    /**
     * обработка исключений ParameterValidationException, ValidationException
     * генерирует код ошибки HTTP HttpStatus.BAD_REQUEST
     *
     * @param e исключение RuntimeException
     * @return объект ErrorResponse
     */
    @ExceptionHandler({ParameterValidationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse parameterValidationExceptionHandler(RuntimeException e) {
        log.info(e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    /**
     * обработка исключения FilmNotFoundException
     * генерирует код ошибки HTTP HttpStatus.NOT_FOUND
     *
     * @param e исключение FilmNotFoundException
     * @return объект ErrorResponse
     */
    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse filmNotFoundExceptionHandler(RuntimeException e) {
        log.info(e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    /**
     * обработка исключений NullPointerException, IllegalArgumentException
     * генерирует код ошибки HTTP HttpStatus.INTERNAL_SERVER_ERROR
     *
     * @param e исключение RuntimeException
     * @return объект ErrorResponse
     */
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverExceptionHandler(RuntimeException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }
}