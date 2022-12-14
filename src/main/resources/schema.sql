CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
      USER_ID INTEGER NOT NULL AUTO_INCREMENT,
      EMAIL CHARACTER VARYING(60) NOT NULL,
      LOGIN CHARACTER VARYING(60) NOT NULL,
      NAME CHARACTER VARYING(60) NOT NULL,
      BIRTHDAY DATE NOT NULL,
      CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.USER_FRIENDS (
    USER_ID INTEGER NOT NULL,
    FRIEND_ID INTEGER NOT NULL,
    CONFIRMED BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT USER_FRIENDS_PK PRIMARY KEY (USER_ID, FRIEND_ID),
    CONSTRAINT USER_FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE,
    CONSTRAINT USER_FRIENDS_FK2 FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.MPA_RATINGS (
    MPA_RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
    MPA_RATING_NAME CHARACTER VARYING(10) NOT NULL,
    CONSTRAINT MPA_RATING_PK PRIMARY KEY (MPA_RATING_ID),
    CONSTRAINT MPA_RATING_UN UNIQUE (MPA_RATING_NAME)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
    FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
    NAME CHARACTER VARYING(60) NOT NULL,
    DESCRIPTION CHARACTER VARYING(200) NOT NULL,
    RELEASE_DATE DATE NOT NULL,
    DURATION INTEGER DEFAULT 0 NOT NULL,
    MPA_RATING_ID INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID),
    CONSTRAINT FILMS_FK FOREIGN KEY (MPA_RATING_ID) REFERENCES PUBLIC.MPA_RATINGS(MPA_RATING_ID) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_LIKES (
    FILM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CONSTRAINT FILM_LIKES_UN UNIQUE (FILM_ID, USER_ID),
    CONSTRAINT FILM_LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID) ON DELETE CASCADE,
    CONSTRAINT FILM_LIKES_FK2 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
    GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
    GENRE_NAME CHARACTER VARYING(60) NOT NULL,
    CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID),
    CONSTRAINT GENRES_UN UNIQUE (GENRE_NAME)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRES (
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
    CONSTRAINT FILM_GENRES_PK PRIMARY KEY (FILM_ID, GENRE_ID),
    CONSTRAINT FILM_GENRES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID) ON DELETE CASCADE,
    CONSTRAINT FILM_GENRES_FK2 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(GENRE_ID) ON DELETE CASCADE
);
