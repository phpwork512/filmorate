package ru.yandex.practicum.filmorate.exceptions;

public class ParameterValidationException extends RuntimeException {
    public ParameterValidationException() {
        super();
    }

    public ParameterValidationException(String message) {
        super(message);
    }

    public ParameterValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterValidationException(Throwable cause) {
        super(cause);
    }
}
