# java-filmorate
Template repository for Filmorate project.

## Схема БД приложения Filmorate
![Схема БД приложения Filmorate](https://github.com/phpwork512/filmorate/blob/main/Filmorate%20DB%20Scheme.png?raw=true)

## Примеры запросов
1. Получить топ-10 фильмов по лайкам

SELECT Films.*, COUNT(FilmLikes.FilmId) AS likes
FROM Films LEFT JOIN FilmLikes ON Films.Id=FilmLikes.FilmId 
GROUP BY Films.Id
ORDER BY likes DESC
LIMIT 10;

2. Получить список друзей пользователя с id=1

SELECT Users.* 
FROM Users JOIN UserFriends ON Users.Id = UserFriends.FriendId 
WHERE UserFriends.UserId = 1

3. Общие друзья пользователей с id=1 и id=2

SELECT Users.*, COUNT(UserFriends.FriendId) AS cnt
FROM UserFriends JOIN Users ON Users.Id = UserFriends.FriendId
WHERE UserFriends.UserId IN (1,2) AND UserFriends.Confirmed=1
GROUP BY UserFriends.FriendId
HAVING cnt=2;

4. Получить жанры фильма с id=2

SELECT Genre.*
FROM Films JOIN FilmGenres ON Films.Id = FilmGenres.FilmId
	JOIN Genre ON FilmGenres.GenreId = Genre.Id
WHERE Films.Id = 2;
