package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ParameterValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.ErrorResponse;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * вернуть данные по всем пользователям
     *
     * @return список объектов типа User
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    /**
     * вернуть данные пользователя с указанным id
     *
     * @param userId id пользователя
     * @return объект типа User, статус OK если всё хорошо, статус NOT_FOUND если такого пользователя нет
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable int userId) {
        User user = userService.getById(userId);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("Пользователь с указанным id не найден: " + userId);
        }
    }

    /**
     * создает пользователя, проверяет валидность полученных данных, присваивает уникальный id
     *
     * @param user заполненный данными объект класса User
     * @return объект класса user, статус либо CREATED если всё ок, либо BAD_REQUEST если есть ошибки в данных
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        log.info(user.toString());
        UserValidator.validate(user);

        //имя для отображения может быть пустым — в таком случае будет использован логин;
        String name = user.getName();
        if (name == null || name.isBlank()) user.setName(user.getLogin());

        return userService.create(user);
    }

    /**
     * обновляет пользователя, проверяет валидность полученных данных
     *
     * @param user заполненный данными объект класса User
     * @return заполненный данными объект класса User, статус OK если всё ок
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@RequestBody User user) {
        log.info(user.toString());
        UserValidator.validate(user);

        //имя для отображения может быть пустым — в таком случае будет использован логин;
        String name = user.getName();
        if (name == null || name.isBlank()) user.setName(user.getLogin());

        User updatedUser = userService.update(user);
        if (updatedUser != null) {
            return updatedUser;
        } else {
            throw new UserNotFoundException("Пользователь с указанным id не найден: " + user.getId());
        }
    }

    /**
     * добавляет пользователя в друзья
     *
     * @param userId   id пользователя кто добавляет
     * @param friendId id пользователя кого добавляют
     */
    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriendById(userId, friendId);
    }

    /**
     * удаляет пользователя из друзей
     *
     * @param userId   id пользователя кто добавляет
     * @param friendId id пользователя кого добавляют
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeFriendById(userId, friendId);
    }

    /**
     * возвращает список друзей пользователя
     *
     * @param userId id пользователя
     * @return список друзей в виде объектов типа User
     */
    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendList(@PathVariable int userId) {
        return userService.getUserFriends(userId);
    }

    /**
     * возвращает список взаимных друзей
     *
     * @param userId  id пользователя 1
     * @param otherId id пользователя 2
     * @return список взаимных друзей в виде объектов типа User
     */
    @GetMapping("/{userId}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getMutualFriendList(@PathVariable int userId, @PathVariable int otherId) {
        return userService.getMutualFriendsById(userId, otherId);
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
     * обработка исключения UserNotFoundException
     * генерирует код ошибки HTTP HttpStatus.NOT_FOUND
     *
     * @param e исключение UserNotFoundException
     * @return объект ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundExceptionHandler(UserNotFoundException e) {
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