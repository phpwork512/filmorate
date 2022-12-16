package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storage.dictionary.GenresDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenresDbStorage genresDbStorage;

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