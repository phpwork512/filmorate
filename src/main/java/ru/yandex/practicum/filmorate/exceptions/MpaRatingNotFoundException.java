package ru.yandex.practicum.filmorate.exceptions;

public class MpaRatingNotFoundException extends RuntimeException {
    public MpaRatingNotFoundException() {
        super();
    }

    public MpaRatingNotFoundException(String message) {
        super(message);
    }

    public MpaRatingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MpaRatingNotFoundException(Throwable cause) {
        super(cause);
    }
}