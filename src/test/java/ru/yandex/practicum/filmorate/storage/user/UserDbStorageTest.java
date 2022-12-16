package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static boolean dbIsInitialized = false;

    /**
     * начальная инициализация базы для тестов
     */
    @BeforeEach
    public void setUpUserRelatedTables() {
        if (!dbIsInitialized) { //нужно заполнить таблицы только один раз сразу для всех тестов тут
            dbIsInitialized = true;

            userStorage.clearAll();

            //запись id=1
            jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)"
                    , "aa@mm.ru"
                    , "testlogin"
                    , "Name"
                    , LocalDate.of(2012, 12, 12).toString());

            //запись id=2
            jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)"
                    , "bb@mm.ru"
                    , "testlogin2"
                    , "Name2"
                    , LocalDate.of(2012, 11, 12).toString());

            //запись id=3
            jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)"
                    , "cc@mm.ru"
                    , "testlogin3"
                    , "Name3"
                    , LocalDate.of(2012, 10, 12).toString());
        }
    }

    /**
     * тест на запрос существующей записи
     */
    @Test
    void getById() {
        User user = userStorage.getById(1);
        assertNotNull(user);
        assertEquals("aa@mm.ru", user.getEmail());
        assertEquals("testlogin", user.getLogin());
        assertEquals("Name", user.getName());
        assertEquals(LocalDate.of(2012, 12, 12), user.getBirthday());
    }

    /**
     * Тест на запрос несуществующей записи, должно вернуть null
     */
    @Test
    void getByIdIfNoSuchRecord() {
        User user = userStorage.getById(100);
        assertNull(user);
    }

    /**
     * тест на получение пользователей с id из списка
     */
    @Test
    void getByIdList() {
        List<User> users = userStorage.getByIdList(List.of(1, 3));
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals("aa@mm.ru", users.get(0).getEmail());
        assertEquals(3, users.get(1).getId());
        assertEquals("cc@mm.ru", users.get(1).getEmail());
    }

    /**
     * тест на получение всех пользователей
     */
    @Test
    void getAll() {
        List<User> users = userStorage.getAll();
        assertNotNull(users);
        assertEquals(4, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals("aa@mm.ru", users.get(0).getEmail());
        assertEquals(2, users.get(1).getId());
        assertEquals("bb@mm.ru", users.get(1).getEmail());
        assertEquals(3, users.get(2).getId());
        assertEquals("cc@mm.ru", users.get(2).getEmail());
    }

    /**
     * тест на создание пользователя
     */
    @Test
    void create() {
        User user = userStorage.create(new User("test@email.com", "login", "New test name", LocalDate.of(2000, 1, 1)));
        assertNotNull(user);
        assertEquals("test@email.com", user.getEmail());
        assertEquals("login", user.getLogin());
        assertEquals("New test name", user.getName());
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthday());
    }

    /**
     * тест на обновление пользователя
     */
    @Test
    void update() {
        User user = userStorage.update(new User(4, "test-new@email.com", "login-new", "New test name new", LocalDate.of(2000, 1, 2)));
        assertNotNull(user);

        user = userStorage.getById(4);
        assertNotNull(user);
        assertEquals("test-new@email.com", user.getEmail());
        assertEquals("login-new", user.getLogin());
        assertEquals("New test name new", user.getName());
        assertEquals(LocalDate.of(2000, 1, 2), user.getBirthday());
    }

    /**
     * тест на добавление дружбы
     */
    @Test
    void addFriend() {
        User user = userStorage.getById(1);
        assertNotNull(user);

        userStorage.addFriend(user, 2);
        user = userStorage.getById(1);
        assertNotNull(user);
        assertEquals(1, user.getFriendIdSet().size());
        assertTrue(user.getFriendIdSet().contains(2));

        user = userStorage.getById(2);
        assertNotNull(user);
        assertEquals(0, user.getFriendIdSet().size());
        assertFalse(user.getFriendIdSet().contains(1));
    }

    /**
     * тест на удаление дружбы
     */
    @Test
    void removeFriend() {
        User user = userStorage.getById(1);
        assertNotNull(user);

        userStorage.addFriend(user, 2);
        user = userStorage.getById(1);
        assertNotNull(user);
        assertEquals(1, user.getFriendIdSet().size());
        assertTrue(user.getFriendIdSet().contains(2));

        userStorage.removeFriend(user, 2);
        user = userStorage.getById(1);
        assertNotNull(user);
        assertEquals(0, user.getFriendIdSet().size());
        assertFalse(user.getFriendIdSet().contains(2));
    }
}