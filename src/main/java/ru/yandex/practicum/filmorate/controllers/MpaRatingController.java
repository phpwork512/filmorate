package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.ErrorResponse;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaRatingController {
    private final MpaRatingService mpaRatingService;

    /**
     * вернуть данные по всем рейтингам
     *
     * @return список объектов типа MpaRating
     */
    @GetMapping
    public List<MpaRating> getAllRatings() {
        return mpaRatingService.getAll();
    }

    /**
     * вернуть данные рейтинга MPA с указанным id
     *
     * @param mpaRatingId id рейтинга в таблице mpa_ratings
     * @return объект типа MpaRating, статус OK если всё хорошо
     */
    @GetMapping("/{mpaRatingId}")
    @ResponseStatus(HttpStatus.OK)
    public MpaRating getRatingById(@PathVariable int mpaRatingId) {
        MpaRating mpaRating = mpaRatingService.getById(mpaRatingId);
        if (mpaRating != null) {
            return mpaRating;
        } else {
            throw new MpaRatingNotFoundException("Рейтинг MPA с указанным id не найден: " + mpaRatingId);
        }
    }

    /**
     * обработка исключения MpaRatingNotFoundException
     * генерирует код ошибки HTTP HttpStatus.NOT_FOUND
     *
     * @param e исключение MpaRatingNotFoundException
     * @return объект ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse mpaRatingNotFoundExceptionHandler(MpaRatingNotFoundException e) {
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