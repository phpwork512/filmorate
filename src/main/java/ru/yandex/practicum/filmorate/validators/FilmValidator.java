package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilmValidator {
    /**
     * метод служит для валидации данных фильма
     *
     * @param film объект для проверки
     * @throws ValidationException в случае если валидация неуспешна
     */
    public static void validate(Film film) throws ValidationException, IllegalArgumentException {
        //валидация модели с использованием аннотаций
        Set<ConstraintViolation<Film>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(film);
        if (violations.size() > 0) {
            List<String> errors = new ArrayList<>();
            for (ConstraintViolation<Film> violation : violations) {
                errors.add("Поле " + violation.getPropertyPath().toString() + " " + violation.getMessage());
            }
            throw new ValidationException(String.join("\n", errors));
        }

        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма раньше 28 декабря 1895 года");
        }
    }
}