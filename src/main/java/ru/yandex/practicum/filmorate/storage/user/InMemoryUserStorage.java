package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    /**
     * Map для хранения данных
     */
    private final Map<Integer, User> users = new HashMap<>();

    /**
     * счетчик для генерации уникальных id
     */
    private int newId = 0;

    /**
     * получить данные пользователя по его ID
     *
     * @param id ID пользователя
     * @return объект типа User или null если такой ID не найден
     */
    @Override
    public User getById(int id) {
        return users.get(id);
    }

    /**
     * получить список всех пользователей
     *
     * @return список объектов User
     */
    @Override
    public List<User> getAll() {
        return new ArrayList<User>(users.values());
    }

    /**
     * создать запись пользователя в хранилище, присвоить уникальный id
     *
     * @param user заполненный объект типа User (кроме поля id)
     * @return заполненный объект типа User
     */
    @Override
    public User create(User user) {
        user.setId(++newId);
        users.put(user.getId(), user);
        return user;
    }

    /**
     * обновить запись пользователя в хранилище, поиск по id
     *
     * @param user заполненный объект типа User
     * @return заполненный объект типа User или null если такого пользователя нет в хранилище
     */
    @Override
    public User update(User user) {
        int userId = user.getId();

        if (users.containsKey(userId)) {
            //заменяя объект в хранилище на обновлённый сохраняем старый набор лайков
            user.setFriendIdSet(users.get(userId).getFriendIdSet());
            users.put(userId, user);
            return user;
        } else {
            return null;
        }

    }

    /**
     * Очистить хранилище
     */
    @Override
    public void clearAll() {
        users.clear();
        newId = 0;
    }
}