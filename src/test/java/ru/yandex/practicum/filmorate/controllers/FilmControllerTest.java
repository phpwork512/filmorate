package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmStorage filmStorage;

    @AfterEach
    private void resetStorage() {
        filmStorage.clearAll();
    }

    //получить список объектов
    // эндпоинт GET /films
    @Test
    void getAllFilms() throws Exception {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        mvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(film))));
    }

    //сохранить в контроллере объект с валидными полями
    //эндпоинт POST /films
    @Test
    void createValid() throws Exception {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся сохранить в контроллере объект с пустым названием
    //эндпоинт POST /films
    @Test
    void createEmptyName() throws Exception {
        Film film = new Film("", "b", LocalDate.now().minusYears(1), 99);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся сохранить в контроллере объект с длинным описанием > 200 символов
    //эндпоинт POST /films
    @Test
    void createLongDescription() throws Exception {
        Film film = new Film("a", "b".repeat(201), LocalDate.now().minusYears(1), 99);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся сохранить в контроллере объект с длинным описанием, но равным 200 символов
    //эндпоинт POST /films
    @Test
    void createDescription200Chars() throws Exception {
        Film film = new Film("a", "b".repeat(200), LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся сохранить в контроллере объект с датой релиза раньше 28 декабря 1895 года
    //эндпоинт POST /films
    @Test
    void createReleaseDateBefore_28_12_1895() throws Exception {
        Film film = new Film("a", "b", LocalDate.of(1895, 12, 27), 99);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся сохранить в контроллере объект с датой релиза 28 декабря 1895 года
    //эндпоинт POST /films
    @Test
    void createReleaseDateEquals_28_12_1895() throws Exception {
        Film film = new Film("a", "b", LocalDate.of(1895, 12, 28), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся сохранить в контроллере объект с отрицательной длительностью
    //эндпоинт POST /films
    @Test
    void createNegativeDuration() throws Exception {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), -1);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся сохранить в контроллере объект с нулевой длительностью
    //эндпоинт POST /films
    @Test
    void createZeroDuration() throws Exception {
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 0);
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся сохранить в контроллере null
    //эндпоинт POST /films
    @Test
    void createNullPointer() throws Exception {
        mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //обновить в контроллере объект с валидными полями
    //эндпоинт PUT /films
    @Test
    void updateValid() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setName("aa");
        film.setDescription("bb");
        film.setReleaseDate(LocalDate.now().minusYears(2));
        film.setDuration(98);
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся обновить в контроллере объект, присвоить пустое название
    //эндпоинт PUT /films
    @Test
    void updateEmptyName() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setName("");
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся обновить в контроллере объект, установить длинное описание > 200 символов
    //эндпоинт PUT /films
    @Test
    void updateLongDescription() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setDescription("b".repeat(201));
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся обновить в контроллере объект, установить длинное описание, но равное 200 символов
    //эндпоинт PUT /films
    @Test
    void updateDescription200Chars() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setDescription("b".repeat(200));
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся обновить в контроллере объект, установить дату релиза раньше 28 декабря 1895 года
    //эндпоинт PUT /films
    @Test
    void updateReleaseDateBefore_28_12_1895() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся обновить в контроллере объект, установить дату релиза равной 28 декабря 1895 года
    //эндпоинт PUT /films
    @Test
    void updateReleaseDateEquals_28_12_1895() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    //пытаемся обновить в контроллере объект, установить отрицательную длительность
    //эндпоинт PUT /films
    @Test
    void updateNegativeDuration() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setDuration(-10);
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся обновить в контроллере объект, установить нулевую длительность
    //эндпоинт PUT /films
    @Test
    void updateZeroDuration() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);
        ResultActions resultActions = mvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        film.setId(1);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(film)));

        //обновить
        film.setDuration(0);
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //пытаемся передать в контроллер null
    //эндпоинт PUT /films
    @Test
    void updateNullPointer() throws Exception {
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //пытаемся обновить несуществующий объект
    //эндпоинт PUT /films
    @Test
    void updateIfNotExists() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);

        //обновить
        film.setId(1);
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновление с неверным id
    //эндпоинт PUT /films
    @Test
    void updateWithWrongId() throws Exception {
        //создать
        Film film = new Film("a", "b", LocalDate.now().minusYears(1), 99);

        //обновить
        film.setId(-1);
        mvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }
}