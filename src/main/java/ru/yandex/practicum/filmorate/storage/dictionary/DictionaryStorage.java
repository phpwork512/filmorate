package ru.yandex.practicum.filmorate.storage.dictionary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DictionaryStorage<T> {
    /**
     * получить элемент справочника по его id
     *
     * @param id идентификатор элемента справочника
     * @return элемент справочника
     */
    T getById(int id);

    /**
     * получить все элементы справочника
     *
     * @return список элементов справочника
     */
    List<T> getAll();
}