package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * целочисленный идентификатор
     */
    private int id;

    /**
     * электронная почта
     */
    @Email(message = "Электронная почта указана неверно")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    /**
     * логин пользователя
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "[^ ]*", message = "Логин не может содержать пробелы")
    private String login;

    /**
     * имя для отображения
     */
    private String name;

    /**
     * дата рождения
     */
    @Past(message = "Дата рождения должна быть в прошлом")
    @NotNull
    private LocalDate birthday;

    /**
     * набор id друзей пользователя
     */
    @JsonIgnore
    private Set<Integer> friendIdSet = new HashSet<>();

    /*
    Добавьте статус для связи «дружба» между двумя пользователями:
    неподтверждённая — когда один пользователь отправил запрос на добавление другого пользователя в друзья,
    подтверждённая — когда второй пользователь согласился на добавление.
    */

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this(email, login, name, birthday);
        this.id = id;
    }
}