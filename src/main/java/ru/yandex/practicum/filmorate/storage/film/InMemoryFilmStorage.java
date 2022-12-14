package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    /**
     * Map для хранения данных
     */
    private final Map<Integer, Film> films = new HashMap<>();

    /**
     * счетчик для генерации уникальных id
     */
    private int newId = 0;

    @Override
    public Film getById(int id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<Film>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(++newId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();

        if (films.containsKey(filmId)) {
            //заменяя объект в хранилище на обновлённый сохраняем старый набор лайков
            film.setLikedUserIdSet(films.get(filmId).getLikedUserIdSet());
            films.put(filmId, film);
            return film;
        } else {
            return null;
        }
    }

    /**
     * Очистить хранилище
     */
    @Override
    public void clearAll() {
        films.clear();
        newId = 0;
    }

    /**
     * добавить лайк в список лайкнувших фильм пользователей
     *
     * @param film   объект типа Film
     * @param userId id пользователя
     */
    public void addLike(Film film, int userId) {
        film.getLikedUserIdSet().add(userId);
    }

    /**
     * убрать лайк из списка лайкнувших фильм пользователей
     *
     * @param film   объект типа Film
     * @param userId id пользователя
     */
    public void removeLike(Film film, int userId) {
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

        List<Film> filmsSortedByLikes = getAll();
        if (filmsSortedByLikes.size() < count) count = filmsSortedByLikes.size();

        filmsSortedByLikes.sort(Comparator.comparingInt(film -> -1 * film.getLikedUserIdSet().size()));

        return filmsSortedByLikes.subList(0, count);
    }
}