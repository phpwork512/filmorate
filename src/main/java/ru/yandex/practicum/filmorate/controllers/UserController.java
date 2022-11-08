package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int newId = 0;

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        try {
            validateUser(user);

            //имя для отображения может быть пустым — в таком случае будет использован логин;
            String name = user.getName();
            if (name == null || name.isBlank()) user.setName(user.getLogin());

            int userId = ++newId;
            user.setId(userId);
            users.put(userId, user);

            log.info(user.toString());

            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (ValidationException|NullPointerException|IllegalArgumentException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User user) {
        try {
            validateUser(user);

            //имя для отображения может быть пустым — в таком случае будет использован логин;
            String name = user.getName();
            if (name == null || name.isBlank()) user.setName(user.getLogin());

            int userId = user.getId();
            if (userId >= 0) {
                if (users.containsKey(userId)) {
                    users.remove(userId);
                    users.put(userId, user);

                    log.info(user.toString());

                    return new ResponseEntity<>(user, HttpStatus.OK);
                }
                else {
                    log.info("Пользователя с id={} не существует", userId);
                    return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
                }
            }
            else {
                throw new ValidationException("Передан неверный id пользователя");
            }

        } catch (ValidationException|NullPointerException|IllegalArgumentException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * метод служит для валидации данных пользователя
     * @param user объект для проверки
     * @throws ValidationException в случае если валидация неуспешна
     */
    private void validateUser(User user) throws ValidationException, IllegalArgumentException {

        Set<ConstraintViolation<User>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        if (violations.size() > 0) {
            List<String> errors = new ArrayList<>();
            for (ConstraintViolation<User> violation : violations) {
                errors.add("Поле " + violation.getPropertyPath().toString() + " " + violation.getMessage());
            }
            throw new ValidationException(String.join("\n", errors));
        }

        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();

        if ( email == null
            || email.isBlank()
            || !email.contains("@")
            || email.indexOf("@") != email.lastIndexOf("@") ) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать один символ @");
        }

        if ( login == null
            || login.isBlank()
            || login.contains(" ") ) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if ( birthday == null
            || user.getBirthday().isAfter(LocalDate.now()) ) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}