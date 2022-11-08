package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    /** целочисленный идентификатор */
    private int id;

    /** название */
    @NotBlank
    private String name;

    /** описание */
    @Size(min = 0, max = 200)
    private String description;

    /** дата релиза */
    @NotNull
    private LocalDate releaseDate;

    /** продолжительность фильма в минутах*/
    @Min(value = 1)
    private Integer duration;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
