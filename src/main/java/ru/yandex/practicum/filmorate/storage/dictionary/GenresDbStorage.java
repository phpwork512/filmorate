package ru.yandex.practicum.filmorate.storage.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenresDbStorage implements DictionaryStorage<Genre> {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * получить элемент справочника по его id
     *
     * @param id идентификатор элемента справочника
     * @return элемент справочника
     */
    @Override
    public Genre getById(int id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sqlQuery, this::mapFunction, id);

        if (!genreList.isEmpty()) return genreList.get(0);
        else return null;
    }

    /**
     * получить все элементы справочника
     *
     * @return список элементов справочника
     */
    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sqlQuery, this::mapFunction);
    }

    /**
     * метод для маппинга данных запроса в объект
     *
     * @param rs     данные запроса
     * @param rowNum номер строки
     * @return объект
     * @throws SQLException
     */
    private Genre mapFunction(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}