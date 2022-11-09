package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int newId = 0;

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        try {
            FilmValidator.validate(film);

            int filmId = ++newId;
            film.setId(filmId);
            films.put(filmId, film);

            log.info(film.toString());

            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException|NullPointerException|IllegalArgumentException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film film) {
        try {
            FilmValidator.validate(film);

            int filmId = film.getId();
            if (filmId >= 0) {
                if (films.containsKey(filmId)) {
                    films.remove(filmId);
                    films.put(filmId, film);

                    log.info(film.toString());

                    return new ResponseEntity<>(film, HttpStatus.OK);
                }
                else {
                    log.info("Фильм с id={} не существует", filmId);
                    return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
                }
            }
            else {
                throw new ValidationException("Передан неверный id фильма");
            }
        } catch (ValidationException|NullPointerException|IllegalArgumentException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }
}