package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.storage.dictionary.MpaRatingDbStorage;

import java.sql.ResultSet;
import java.util.List;

@Service
public class MpaRatingService {
    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Autowired
    public MpaRatingService(MpaRatingDbStorage mpaRatingDbStorage) {
        this.mpaRatingDbStorage = mpaRatingDbStorage;
    }

    /**
     * получить элемент справочника по его id
     *
     * @param id идентификатор элемента справочника
     * @return элемент справочника
     */
    public MpaRating getById(Integer id) {
        return mpaRatingDbStorage.getById(id);
    }

    /**
     * получить все элементы справочника
     *
     * @return список элементов справочника
     */
    public List<MpaRating> getAll() {
        return mpaRatingDbStorage.getAll();
    }
}