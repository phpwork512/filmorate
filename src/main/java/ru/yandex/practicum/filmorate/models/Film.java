package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    /**
     * целочисленный идентификатор
     */
    private int id = 0;

    /**
     * название
     */

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /**
     * описание
     */
    @NotNull
    @Size(min = 0, max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;

    /**
     * дата релиза
     */
    @NotNull
    private LocalDate releaseDate;

    /**
     * продолжительность фильма в минутах
     */
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    /**
     * рейтинг MPA
     */
    @NotNull
    private MpaRating mpa;

    @NotNull
    private List<Genre> genres = new ArrayList<>();

    /**
     * набор id пользователей кто лайкнул фильм
     */
    @JsonIgnore
    private Set<Integer> likedUserIdSet = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration, MpaRating mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration, MpaRating mpa, List<Genre> genres) {
        this(name, description, releaseDate, duration, mpa, genres);
        this.id = id;
    }
}