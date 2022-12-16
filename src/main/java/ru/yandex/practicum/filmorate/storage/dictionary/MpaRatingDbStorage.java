package ru.yandex.practicum.filmorate.storage.dictionary;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaRatingDbStorage implements DictionaryStorage<MpaRating> {
    private final JdbcTemplate jdbcTemplate;

    /**
     * получить элемент справочника по его id
     *
     * @param id идентификатор элемента справочника
     * @return элемент справочника
     */
    @Override
    public MpaRating getById(int id) {
        String sqlQuery = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
        List<MpaRating> mpaList = jdbcTemplate.query(sqlQuery, this::mapFunction, id);

        if (!mpaList.isEmpty()) return mpaList.get(0);
        else return null;
    }

    /**
     * получить все элементы справочника
     *
     * @return список элементов справочника
     */
    @Override
    public List<MpaRating> getAll() {
        String sqlQuery = "SELECT * FROM mpa_ratings ORDER BY mpa_rating_id";
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
    private MpaRating mapFunction(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name"));
    }
}