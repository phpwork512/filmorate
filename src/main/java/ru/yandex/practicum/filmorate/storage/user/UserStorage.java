package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {
    /**
     * отдать объект с указанным id
     * @param id ид пользователя
     * @return объект типа User или null если объект не найден
     */
    User getById(int id);

    /**
     * Отдать все хранимые объекты в виде списка
     * @return список объектов типа User
     */
    List<User> getAll();

    /**
     * Добавить объект в хранилище, присвоить уникальный id
     * @param user добавляемый объект
     * @return добавленный объект
     */
    User create(User user);

    /**
     * Обновить объект в хранилище, поиск обновляемого объекта по user.id
     * @param user обновляемый объект
     * @return обновленный объект или null если объект по id не найден
     */
    User update(User user);

    /**
     * Очистить хранилище
     */
    public void clearAll();
}
