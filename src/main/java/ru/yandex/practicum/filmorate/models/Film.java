package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    /** целочисленный идентификатор */
    private int id;

    /** название */
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name;

    /** описание */
    @NotNull
    @Size(min = 0, max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;

    /** дата релиза */
    @NotNull
    private LocalDate releaseDate;

    /** продолжительность фильма в минутах*/
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    /** набор id пользователей кто лайкнул фильм */
    @JsonIgnore
    private Set<Integer> likedUserIdSet = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}