package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}