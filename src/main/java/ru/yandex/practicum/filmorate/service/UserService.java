package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ParameterValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Добавить пользователя себе в друзья.
     * Операция не взаимная
     *
     * @param personId id пользователя, кто добавляет в свой список друзей
     * @param friendId id пользователя, кого добавляют
     */
    public void addFriendById(int personId, int friendId) throws ParameterValidationException, UserNotFoundException {
        if (personId == friendId) {
            throw new ParameterValidationException("Запрещено добавлять пользователя в друзья самому себе");
        }

        User person = userStorage.getById(personId);
        if (person == null) {
            throw new UserNotFoundException("Пользователь с id " + personId + " не найден");
        }

        User friend = userStorage.getById(friendId);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с id " + friendId + " не найден");
        }

        person.getFriendIdSet().add(friend.getId());
        userStorage.addFriend(person, friend.getId());
    }

    /**
     * Удалить пользователя из друзей.
     * Операция не взаимная
     *
     * @param personId id пользователя, кто удаляет
     * @param friendId id пользователя, кого добавляют
     */
    public void removeFriendById(int personId, int friendId) throws UserNotFoundException {
        User person = userStorage.getById(personId);
        if (person == null) {
            throw new UserNotFoundException("Пользователь с id " + personId + " не найден");
        }

        User friend = userStorage.getById(friendId);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с id " + friendId + " не найден");
        }

        person.getFriendIdSet().remove(friend.getId());
        userStorage.removeFriend(person, friend.getId());
    }

    /**
     * вернуть список друзей пользователя
     *
     * @param userId id пользователя
     * @return список друзей в виде объектов типа User
     */
    public List<User> getUserFriends(int userId) throws UserNotFoundException {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        return userStorage.getByIdList(new ArrayList<Integer>(user.getFriendIdSet()));
    }

    /**
     * Отдать список общих друзей двух пользователей
     *
     * @param userId1 id пользователя 1
     * @param userId2 id пользователя 2
     * @return список объектов типа User, которые являются общими друзьями заданных пользователей
     */
    public List<User> getMutualFriendsById(int userId1, int userId2) throws UserNotFoundException {
        User user1 = userStorage.getById(userId1);
        if (user1 == null) {
            throw new UserNotFoundException("Пользователь с id " + userId1 + " не найден");
        }

        User user2 = userStorage.getById(userId2);
        if (user2 == null) {
            throw new UserNotFoundException("Пользователь с id " + userId2 + " не найден");
        }

        Set<Integer> friendIdList1 = user1.getFriendIdSet();
        Set<Integer> friendIdList2 = user2.getFriendIdSet();

        Set<Integer> intersection = new HashSet<>(friendIdList1);
        intersection.retainAll(friendIdList2);

        return userStorage.getByIdList(new ArrayList<Integer>(intersection));
    }

    /**
     * получить список всех пользователей
     *
     * @return список объектов User
     */
    public List<User> getAll() {
        return userStorage.getAll();
    }

    /**
     * получить данные пользователя по его ID
     *
     * @param userId ID пользователя
     * @return объект типа User или null если такой ID не найден
     */
    public User getById(int userId) {
        return userStorage.getById(userId);
    }

    /**
     * создать запись пользователя в хранилище, присвоить уникальный id
     *
     * @param user заполненный объект типа User (кроме поля id)
     * @return заполненный объект типа User
     */
    public User create(User user) {
        return userStorage.create(user);
    }

    /**
     * обновить запись пользователя в хранилище, поиск по id
     *
     * @param user заполненный объект типа User
     * @return заполненный объект типа User
     */
    public User update(User user) {
        return userStorage.update(user);
    }
}