package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {
    /**
     * отдать объект с указанным id
     *
     * @param id ид пользователя
     * @return объект типа User или null если объект не найден
     */
    User getById(int id);

    /**
     * отдать объекты с указанными в списке id
     *
     * @param idList список ид пользователей
     * @return список объектов типа User
     */
    List<User> getByIdList(List<Integer> idList);

    /**
     * Отдать все хранимые объекты в виде списка
     *
     * @return список объектов типа User
     */
    List<User> getAll();

    /**
     * Добавить объект в хранилище, присвоить уникальный id
     *
     * @param user добавляемый объект
     * @return добавленный объект
     */
    User create(User user);

    /**
     * Обновить объект в хранилище, поиск обновляемого объекта по user.id
     *
     * @param user обновляемый объект
     * @return обновленный объект или null если объект по id не найден
     */
    User update(User user);

    /**
     * Очистить хранилище
     */
    void clearAll();

    /**
     * Метод для добавления дружбы
     *
     * @param user     объект типа User кому добавляем друга
     * @param friendId id пользователя, которого добавляем в друзья
     */
    void addFriend(User user, int friendId);

    /**
     * Метод для удаления дружбы
     *
     * @param user     объект типа User кому удаляем друга
     * @param friendId id пользователя, которого удаляем из друзей
     */
    void removeFriend(User user, int friendId);
}