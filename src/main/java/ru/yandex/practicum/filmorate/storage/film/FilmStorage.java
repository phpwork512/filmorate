package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmStorage {
    /**
     * отдать объект с указанным id
     * @param id ид фильма
     * @return объект типа Film или null если объект не найден
     */
    Film getById(int id);

    /**
     * Отдать все хранимые объекты в виде списка
     * @return список объектов типа Film
     */
    List<Film> getAll();

    /**
     * Добавить объект в хранилище, присвоить уникальный id
     * @param film добавляемый объект
     * @return добавленный объект
     */
    Film create(Film film);

    /**
     * Обновить объект в хранилище, поиск обновляемого объекта по film.id
     * @param film обновляемый объект
     * @return обновленный объект или null если объект по id не найден
     */
    Film update(Film film);

    /**
     * Очистить хранилище
     */
    public void clearAll();
}
