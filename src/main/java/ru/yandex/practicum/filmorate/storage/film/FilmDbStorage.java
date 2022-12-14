package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * отдать объект с указанным id
     *
     * @param id ид фильма
     * @return объект типа Film или null если объект не найден
     */
    @Override
    public Film getById(int id) {
        String sqlQuery = "SELECT f.*, mpa.mpa_rating_name " +
                "FROM films AS f LEFT JOIN mpa_ratings as mpa ON f.mpa_rating_id = mpa.mpa_rating_id " +
                "WHERE film_id = ?";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapFunction, id);

        //если запись была получена
        if (!filmList.isEmpty()) {
            loadGenresToFilms(filmList);
            loadLikedUsersToFilms(filmList);
            return filmList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Отдать все хранимые объекты Film в виде списка
     *
     * @return список объектов типа Film
     */
    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, mpa.mpa_rating_name " +
                "FROM films AS f LEFT JOIN mpa_ratings as mpa ON f.mpa_rating_id = mpa.mpa_rating_id " +
                "ORDER BY f.film_id";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapFunction);

        //если фильмы были загружены - также загрузить их жанры
        if (!filmList.isEmpty()) {
            loadGenresToFilms(filmList);
            loadLikedUsersToFilms(filmList);
        }

        return filmList;
    }

    /**
     * метод для маппинга данных запроса из таблицы films в объект, список жанров не присваивается
     *
     * @param rs данные запроса
     * @return объект
     * @throws SQLException
     */
    private Film mapFunction(ResultSet rs, int rowNum) throws SQLException {
        return new Film(rs.getInt("film_id")
                , rs.getString("name")
                , rs.getString("description")
                , rs.getDate("release_date").toLocalDate()
                , rs.getInt("duration")
                , new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name"))
                , new ArrayList<Genre>());
    }

    /**
     * метод возвращает список id жанров фильма с указанным id
     *
     * @param filmId id фильма
     * @return список id жанров указанного фильма
     */
    private List<Genre> getGenresOfFilm(Integer filmId) {
        String sqlQuery = "SELECT g.* " +
                "FROM film_genres AS fg LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, (ResultSet rs, int rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId);
    }

    /**
     * метод загружает из БД и записывает в объекты данные о жанрах для переданного списка фильмов, текущий список жанров в объекте очищается
     *
     * @param filmList список объектов типа Film
     */
    private void loadGenresToFilms(List<Film> filmList) {
        Map<Integer, Film> filmMap = new HashMap<>();

        for (Film film : filmList) {
            filmMap.put(film.getId(), film);
            film.getGenres().clear();
        }

        String sqlQuery = "SELECT fg.*, g.genre_name " +
                "FROM film_genres AS fg LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id IN ("
                + filmMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","))
                + ")";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            Film film = filmMap.get(rowSet.getInt("film_id"));
            film.getGenres().add(new Genre(rowSet.getInt("genre_id"), rowSet.getString("genre_name")));
        }
    }

    /**
     * Добавить новый объект в хранилище, присвоить уникальный id.
     * Обновляются жанры, лайки не обновляются
     *
     * @param film добавляемый объект
     * @return добавленный объект
     */
    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        updateGenresOfFilm(film, true);

        return getById(film.getId());
    }

    /**
     * Метод для записи списка жанров фильма в таблицу film_genres.
     * Метод анализирует списки id жанров в БД и в объекте, если они отличаются - то происходит
     * удаление из таблицы film_genres всех старых данных и запись новых.
     * Для новых записей этап анализа существующих данных пропускается и сразу производится запись новых данных.
     *
     * @param film            объект типа Film
     * @param isNewFilmRecord признак работы с новой записью о фильме в БД
     * @return true если была запись новых данных о жанрах фильма в БД, false если не было
     */
    private boolean updateGenresOfFilm(Film film, boolean isNewFilmRecord) {
        Integer filmId = film.getId();
        boolean needInsert = isNewFilmRecord;
        List<Genre> newGenresIdList = film.getGenres();

        if (filmId > 0) {   //для работы с БД в объекте должно быть установлено значение в поле id
            if (!isNewFilmRecord) {
                //загрузить текущий список жанров фильма из БД
                List<Genre> dbGenresIdList = getGenresOfFilm(film.getId());

                //если списки в объекте и БД не совпадают - удаляем в film_genres все ссылки относящиеся к этому фильму
                if (dbGenresIdList.size() != newGenresIdList.size()
                        || !new HashSet<>(dbGenresIdList).containsAll(newGenresIdList)) {
                    deleteGenresOfFilm(film);
                    needInsert = true;
                }
            }

            //если требуется запись новых данных и список id жанров фильма не пуст
            if (needInsert && !newGenresIdList.isEmpty()) {
                List<String> sqlParts = new ArrayList<>();
                for (Genre genre : newGenresIdList) {
                    sqlParts.add(String.format("(%d, %d)", filmId, genre.getId()));
                }

                String sqlQuery = "MERGE INTO film_genres (film_id, genre_id) VALUES " + String.join(",", sqlParts);
                jdbcTemplate.update(sqlQuery);
            }

            return needInsert;
        } else {
            return false;
        }
    }

    /**
     * метод удаляет все ссылки на жанры в таблице film_genres относящиеся к указанному фильму
     *
     * @param film объект типа Film
     */
    private void deleteGenresOfFilm(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    /**
     * Обновить объект в БД, поиск обновляемого объекта по film_id
     * Обновляются жанры, лайки не обновляются
     *
     * @param film обновляемый объект
     * @return обновленный объект или null если объект по id не найден
     */
    @Override
    public Film update(Film film) {
        Integer filmId = film.getId();

        //сначала надо проверить есть ли такой фильм в БД
        if (filmId > 0 && getById(filmId) != null) {
            //фильм есть, обновляем
            String sqlQuery = "UPDATE films " +
                    "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                    "WHERE film_id = ?";

            jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate().toString()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getId());

            updateGenresOfFilm(film, false);

            return getById(film.getId());
        } else {
            return null;
        }
    }

    /**
     * Очистить таблицу фильмов в БД
     */
    @Override
    public void clearAll() {
        String sqlQuery = "DELETE FROM films; "
                + "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    /**
     * Прочитать из БД наборы id пользователей, которые поставили лайк фильмам
     *
     * @param filmList список фильмов для которых нужно прочитать лайки
     */
    private void loadLikedUsersToFilms(List<Film> filmList) {
        if (!filmList.isEmpty()) {
            Map<Integer, Film> filmMap = new HashMap<>();
            for (Film film : filmList) {
                filmMap.put(film.getId(), film);
            }

            String sqlQuery = "SELECT film_id, user_id FROM film_likes WHERE film_id IN ("
                    + filmMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","))
                    + ")";
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);

            while (rowSet.next()) {
                Film film = filmMap.get(rowSet.getInt("film_id"));
                film.getLikedUserIdSet().add(rowSet.getInt("user_id"));
            }
        }
    }

    /**
     * добавить лайк в список лайкнувших фильм пользователей
     *
     * @param film   объект типа Film
     * @param userId id пользователя
     */
    public void addLike(Film film, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film.getId(), userId);
    }

    /**
     * убрать лайк из списка лайкнувших фильм пользователей
     *
     * @param film   объект типа Film
     * @param userId id пользователя
     */
    public void removeLike(Film film, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), userId);
    }

    /**
     * вернуть топ N фильмов по количеству лайков
     *
     * @param count количество фильмов в списке, если не указано или меньше 1 - то берется 10
     * @return список фильмов с самым большим количеством лайков
     */
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.*, mpa.mpa_rating_name, COUNT(fl.film_id) as cnt " +
                "FROM films AS f LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT JOIN mpa_ratings as mpa ON f.mpa_rating_id = mpa.mpa_rating_id " +
                "GROUP BY f.film_id " +
                "ORDER BY cnt DESC " +
                "LIMIT ?";

        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapFunction, count == null || count < 1 ? 10 : count);

        if (!filmList.isEmpty()) {
            loadGenresToFilms(filmList);
            loadLikedUsersToFilms(filmList);
        }

        return filmList;
    }
}