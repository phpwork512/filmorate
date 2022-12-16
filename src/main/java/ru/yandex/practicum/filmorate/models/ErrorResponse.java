package ru.yandex.practicum.filmorate.models;

import lombok.Data;

@Data
public class ErrorResponse {
    // название ошибки
    private final String error;
    // подробное описание
    private final String description;
}