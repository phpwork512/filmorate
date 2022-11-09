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
import ru.yandex.practicum.filmorate.validators.UserValidator;

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
            UserValidator.validate(user);

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
            UserValidator.validate(user);

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
}