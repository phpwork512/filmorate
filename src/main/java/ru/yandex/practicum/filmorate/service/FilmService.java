package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ParameterValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * добавить id пользователя в набор лайкнувших фильм пользователей
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    public void likeFilmById(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.getLikedUserIdSet().add(userId);
    }

    /**
     * удалить id пользователя из набора лайкнувших фильм пользователей
     *
     * @param filmId объект типа Film
     * @param userId объект типа User
     */
    public void dislikeFilmById(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.getLikedUserIdSet().remove(userId);
    }

    /**
     * вернуть топ N фильмов по количеству лайков
     *
     * @param count количество фильмов в списке, если не указано или меньше 1 - то берется 10
     * @return список фильмов с самым большим количеством лайков
     */
    public List<Film> getPopularFilms(Integer count) {
        if (count == null || count < 1) count = 10;

        List<Film> filmsSortedByLikes = filmStorage.getAll();
        if (filmsSortedByLikes.size() < count) count = filmsSortedByLikes.size();

        filmsSortedByLikes.sort(Comparator.comparingInt(film -> -1 * film.getLikedUserIdSet().size()));

        return filmsSortedByLikes.subList(0, count);
    }

    /**
     * получить список всех фильмов
     *
     * @return список объектов Film
     */
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    /**
     * получить данные фильма по его ID
     *
     * @param filmId ID фильма
     * @return объект типа Film или null если такой ID не найден
     */
    public Film getById(int filmId) {
        return filmStorage.getById(filmId);
    }

    /**
     * создать запись фильма в хранилище, присвоить уникальный id
     *
     * @param film заполненный объект типа Film (кроме поля id)
     * @return заполненный объект типа Film
     */
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    /**
     * обновить запись фильма в хранилище, поиск по id
     *
     * @param film заполненный объект типа Film
     * @return заполненный объект типа Film
     */
    public Film update(Film film) {
        return filmStorage.update(film);
    }
}