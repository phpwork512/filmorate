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

class UserControllerTest {
    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    //получить список объектов
    // эндпоинт GET /users
    @Test
    void getAllUsers() {
        ResponseEntity<User> createdEntity = userController.create(new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18)));
        assertEquals(createdEntity.getStatusCode(), HttpStatus.CREATED);

        List<User> users = userController.getAllUsers();
        assertEquals(users.get(0), createdEntity.getBody());
    }

    //сохранить в контроллере объект с валидными полями
    //эндпоинт POST /users
    @Test
    void createValid() {
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с пустым e-mail
    //эндпоинт POST /users
    @Test
    void createWithEmptyEmail() {
        User user = new User("", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с e-mail без @
    //эндпоинт POST /users
    @Test
    void createWithEmailWithoutAt() {
        User user = new User("aaa?bb", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с e-mail с двумя @
    //эндпоинт POST /users
    @Test
    void createWithEmailWithTwoAt() {
        User user = new User("aa@a?b@b", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с e-mail без доменной части
    //эндпоинт POST /users
    @Test
    void createWithEmailWithoutDomain() {
        User user = new User("это-неправильный?эмейл@", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с пустым логином
    //эндпоинт POST /users
    @Test
    void createWithEmptyLogin() {
        User user = new User("aa@mm.ru", "", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с логином с пробелом
    //эндпоинт POST /users
    @Test
    void createWithLoginWithSpace() {
        User user = new User("aa@mm.ru", "a b", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с пустым именем
    //должен подставиться логин
    //эндпоинт POST /users
    @Test
    void createWithEmptyName() {
        User user = new User("aa@mm.ru", "a", "", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        user.setName(user.getLogin());
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //сохранить в контроллере объект с датой рождения в будущем
    //эндпоинт POST /users
    @Test
    void createWithBirthdayInFuture() {
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().plusDays(1));
        ResponseEntity<User> createdEntity = userController.create(user);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());
    }

    //передать в контроллер указатель на null
    //эндпоинт POST /users
    @Test
    void createWithNull() {
        ResponseEntity<User> createdEntity = userController.create(null);

        assertEquals(HttpStatus.BAD_REQUEST, createdEntity.getStatusCode());
        assertNull(createdEntity.getBody());
    }
















    //обновить в контроллере объект с валидными полями
    //эндпоинт PUT /users
    @Test
    void updateValid() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setEmail("bb@zz.ru");
        user.setLogin("aa");
        user.setName("bb");
        user.setBirthday(LocalDate.now().minusYears(17));
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с пустым e-mail
    //эндпоинт PUT /users
    @Test
    void updateWithEmptyEmail() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setEmail("");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с e-mail без @
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithoutAt() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setEmail("aa?mm.ru");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с e-mail с двумя @
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithTwoAt() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setEmail("aa@mm@zz.ru");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с e-mail без доменной части
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithoutDomain() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setEmail("это-неправильный?эмейл@");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с пустым логином
    //эндпоинт PUT /users
    @Test
    void updateWithEmptyLogin() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setLogin("");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с логином с пробелом
    //эндпоинт PUT /users
    @Test
    void updateWithLoginWithSpace() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setLogin("a a");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с пустым именем
    //в поле имя должен подставиться логин
    //эндпоинт put /users
    @Test
    void updateWithEmptyName() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setName("");
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.OK, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновить в контроллере объект с датой рождения в будущем
    //эндпоинт PUT /users
    @Test
    void updateWithBirthdayInFuture() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResponseEntity<User> createdEntity = userController.create(user);

        user.setId(1);
        assertEquals(HttpStatus.CREATED, createdEntity.getStatusCode());
        assertEquals(user, createdEntity.getBody());

        //обновление
        user.setBirthday(LocalDate.now().plusDays(2));
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //передать в контроллер указатель на null
    //эндпоинт PUT /users
    @Test
    void updateWithNull() {
        //обновление
        ResponseEntity<User> updatedEntity = userController.update(null);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(null, updatedEntity.getBody());
    }

    //обновление с неверным id
    //эндпоинт PUT /users
    @Test
    void updateWithWrongId() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));

        user.setId(-1);
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }

    //обновление с несуществующим id
    //эндпоинт PUT /users
    @Test
    void updateWithNotExistId() {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));

        user.setId(1);
        ResponseEntity<User> updatedEntity = userController.update(user);
        assertEquals(HttpStatus.NOT_FOUND, updatedEntity.getStatusCode());
        assertEquals(user, updatedEntity.getBody());
    }
}