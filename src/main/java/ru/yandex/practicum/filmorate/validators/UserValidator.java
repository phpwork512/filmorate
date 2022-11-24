package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserValidator {
    /**
     * метод служит для валидации данных пользователя
     *
     * @param user объект для проверки
     * @throws ValidationException в случае если валидация неуспешна
     */
    public static void validate(User user) throws ValidationException, IllegalArgumentException {
        //валидация модели с использованием аннотаций
        Set<ConstraintViolation<User>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        if (violations.size() > 0) {
            List<String> errors = new ArrayList<>();
            for (ConstraintViolation<User> violation : violations) {
                errors.add("Поле " + violation.getPropertyPath().toString() + " " + violation.getMessage());
            }
            throw new ValidationException(String.join("\n", errors));
        }
    }
}