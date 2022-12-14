package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.models.ErrorResponse;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenresController {
    private final GenreService genreService;

    @Autowired
    public GenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * вернуть данные по всем рейтингам
     *
     * @return список объектов типа MpaRating
     */
    @GetMapping
    public List<Genre> getAllRatings() {
        return genreService.getAll();
    }

    /**
     * вернуть данные жанра с указанным id
     *
     * @param genreId id рейтинга в таблице mpa_ratings
     * @return объект типа Genre, статус OK если всё хорошо
     */
    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getRatingById(@PathVariable int genreId) {
        Genre genre = genreService.getById(genreId);
        if (genre != null) {
            return genre;
        } else {
            throw new GenreNotFoundException("Жанр с указанным id не найден: " + genreId);
        }
    }

    /**
     * обработка исключения GenreNotFoundException
     * генерирует код ошибки HTTP HttpStatus.NOT_FOUND
     *
     * @param e исключение GenreNotFoundException
     * @return объект ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse genreNotFoundExceptionHandler(GenreNotFoundException e) {
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