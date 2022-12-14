package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.storage.dictionary.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.dictionary.MpaRatingDbStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenresDbStorage genresDbStorage;

    @Autowired
    public GenreService(GenresDbStorage genresDbStorage) {
        this.genresDbStorage = genresDbStorage;
    }

    /**
     * получить элемент справочника по его id
     *
     * @param id идентификатор элемента справочника
     * @return элемент справочника
     */
    public Genre getById(Integer id) {
        return genresDbStorage.getById(id);
    }

    /**
     * получить все элементы справочника
     *
     * @return список элементов справочника
     */
    public List<Genre> getAll() {
        return genresDbStorage.getAll();
    }
}