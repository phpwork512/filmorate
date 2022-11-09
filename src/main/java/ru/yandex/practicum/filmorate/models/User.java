package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** целочисленный идентификатор */
    private int id;

    /** электронная почта */
    @Email(message = "Электронная почта указана неверно")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    /** логин пользователя */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp="[^ ]*", message = "Логин не может содержать пробелы")
    private String login;

    /** имя для отображения */
    private String name;

    /** дата рождения*/
    @Past(message = "Дата рождения должна быть в прошлом")
    @NotNull
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}