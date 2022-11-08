package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    /** целочисленный идентификатор */
    private int id;

    /** электронная почта */
    @Email
    @NotNull
    private String email;

    /** логин пользователя */
    @NotBlank
    private String login;

    /** имя для отображения */
    private String name;

    /** дата рождения*/
    @Past
    @NotNull
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
