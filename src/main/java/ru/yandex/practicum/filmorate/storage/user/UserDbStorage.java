package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * отдать объект с указанным id
     *
     * @param id ид пользователя
     * @return объект типа User или null если объект не найден
     */
    @Override
    public User getById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        List<User> userList = jdbcTemplate.query(sqlQuery, this::mapFunction, id);

        //если запись была получена - записать в объект User список id друзей
        if (!userList.isEmpty()) {
            loadFriendsToUsers(userList, true);
            return userList.get(0);
        } else {
            return null;
        }
    }

    /**
     * отдать объекты с указанными в списке id
     *
     * @param idList список ид пользователей
     * @return список объектов типа User
     */
    @Override
    public List<User> getByIdList(List<Integer> idList) {
        if (!idList.isEmpty()) {
            String sqlQuery = "SELECT * FROM users WHERE user_id IN ("
                    + idList.stream().map(String::valueOf).collect(Collectors.joining(","))
                    + ")";
            List<User> userList = jdbcTemplate.query(sqlQuery
                    , this::mapFunction);

            //если записи были получены - записать в объекты User списки id друзей
            if (!userList.isEmpty()) {
                loadFriendsToUsers(userList, true);
            }

            return userList;
        } else {
            return List.of();
        }
    }

    /**
     * Отдать все хранимые объекты в виде списка
     *
     * @return список объектов типа User
     */
    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users ORDER BY user_id";
        List<User> userList = jdbcTemplate.query(sqlQuery, this::mapFunction);

        //если пользователи были загружены - также загрузить их друзей
        if (!userList.isEmpty()) {
            loadFriendsToUsers(userList, true);
        }

        return userList;
    }

    /**
     * метод для маппинга данных запроса из таблицы users в объект
     *
     * @param rs данные запроса
     * @return объект
     * @throws SQLException
     */
    private User mapFunction(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getInt("user_id")
                , rs.getString("email")
                , rs.getString("login")
                , rs.getString("name")
                , rs.getDate("birthday").toLocalDate());
    }

    /**
     * метод возвращает список id друзей пользователя с указанным id
     *
     * @param userId         id фильма
     * @param addUnconfirmed если true - то будут возвращены все записи, иначе - только подтвержденные (confirmed = true)
     * @return сет id пользователей - друзей
     */
    private Set<Integer> getFriendsOfUser(Integer userId, boolean addUnconfirmed) {
        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ? " + (addUnconfirmed ? "" : " AND confirmed = true ");
        List<Integer> friendIdList = jdbcTemplate.query(sqlQuery, (ResultSet rs, int rowNum) -> rs.getInt("friend_id"), userId);
        return new HashSet<>(friendIdList);
    }

    /**
     * метод загружает из БД и записывает в объекты данные о друзьях для переданного списка пользователй
     *
     * @param userList       список объектов типа User
     * @param addUnconfirmed если true - то будут записаны все записи, иначе - только подтвержденные (confirmed = true)
     */
    private void loadFriendsToUsers(List<User> userList, boolean addUnconfirmed) {
        Map<Integer, User> usersMap = new HashMap<>();

        for (User user : userList) {
            usersMap.put(user.getId(), user);
        }

        String sqlQuery = "SELECT user_id, friend_id FROM user_friends WHERE user_id IN ("
                + usersMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","))
                + ") "
                + (addUnconfirmed ? "" : " AND confirmed = true ");
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            User user = usersMap.get(rowSet.getInt("user_id"));
            user.getFriendIdSet().add(rowSet.getInt("friend_id"));
        }
    }

    /**
     * Добавить новый объект в БД, присвоить уникальный id
     *
     * @param user добавляемый объект типа User
     * @return добавленный объект
     */
    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getBirthday().toString());
            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    /**
     * Обновить объект в БД в таблице users, поиск обновляемого объекта по user.id
     * Список друзей этим методом не обновляется
     *
     * @param user обновляемый объект
     * @return обновленный объект или null если объект по id не найден
     */
    @Override
    public User update(User user) {
        Integer userId = user.getId();

        //проверить есть ли такой пользователь в БД
        if (userId > 0 && getById(userId) != null) {
            //пользователь есть, обновляем
            String sqlQuery = "UPDATE users " +
                    "SET email = ?, login = ?, name = ?, birthday = ? " +
                    "WHERE user_id = ?";

            jdbcTemplate.update(sqlQuery
                    , user.getEmail()
                    , user.getLogin()
                    , user.getName()
                    , user.getBirthday().toString()
                    , user.getId());

            return user;
        } else {
            return null;
        }
    }

    /**
     * Очистить таблицу пользователей в БД
     */
    @Override
    public void clearAll() {
        String sqlQuery = "DELETE FROM users; "
                + "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    /**
     * Метод для добавления дружбы
     *
     * @param user     объект типа User кому добавляем друга
     * @param friendId id пользователя, которого добавляем в друзья
     */
    public void addFriend(User user, int friendId) {
        String sqlQuery = "MERGE INTO user_friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), friendId);
    }

    /**
     * Метод для удаления дружбы
     *
     * @param user     объект типа User кому удаляем друга
     * @param friendId id пользователя, которого удаляем из друзей
     */
    public void removeFriend(User user, int friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friendId);
    }
}