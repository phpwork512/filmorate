package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private @Qualifier("UserDbStorage") UserStorage userStorage;

    @AfterEach
    private void resetStorage() {
        userStorage.clearAll();
    }

    //получить список объектов
    // эндпоинт GET /users
    @Test
    void getAllUsers() throws Exception {
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    //сохранить в контроллере объект с валидными полями
    //эндпоинт POST /users
    @Test
    void createValid() throws Exception {
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    //сохранить в контроллере объект с пустым e-mail
    //эндпоинт POST /users
    @Test
    void createWithEmptyEmail() throws Exception {
        User user = new User("", "a", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }


    //сохранить в контроллере объект с e-mail без @
    //эндпоинт POST /users
    @Test
    void createWithEmailWithoutAt() throws Exception {
        User user = new User("aaa?bb", "a", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //сохранить в контроллере объект с e-mail с двумя @
    //эндпоинт POST /users
    @Test
    void createWithEmailWithTwoAt() throws Exception {
        User user = new User("aa@a?b@b", "a", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //сохранить в контроллере объект с e-mail без доменной части
    //эндпоинт POST /users
    @Test
    void createWithEmailWithoutDomain() throws Exception {
        User user = new User("это-неправильный?эмейл@", "a", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //сохранить в контроллере объект с пустым логином
    //эндпоинт POST /users
    @Test
    void createWithEmptyLogin() throws Exception {
        User user = new User("aa@mm.ru", "", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //сохранить в контроллере объект с логином с пробелом
    //эндпоинт POST /users
    @Test
    void createWithLoginWithSpace() throws Exception {
        User user = new User("aa@mm.ru", "a b", "b", LocalDate.now().minusYears(18));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //сохранить в контроллере объект с пустым именем
    //должен подставиться логин
    //эндпоинт POST /users
    @Test
    void createWithEmptyName() throws Exception {
        User user = new User("aa@mm.ru", "a", "", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        user.setName(user.getLogin());
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    //сохранить в контроллере объект с датой рождения в будущем
    //эндпоинт POST /users
    @Test
    void createWithBirthdayInFuture() throws Exception {
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().plusDays(1));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //передать в контроллер указатель на null
    //эндпоинт POST /users
    @Test
    void createWithNull() throws Exception {
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //обновить в контроллере объект с валидными полями
    //эндпоинт PUT /users
    @Test
    void updateValid() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setEmail("bb@zz.ru");
        user.setLogin("aa");
        user.setName("bb");
        user.setBirthday(LocalDate.now().minusYears(17));
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    //обновить в контроллере объект с пустым e-mail
    //эндпоинт PUT /users
    @Test
    void updateWithEmptyEmail() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setEmail("");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с e-mail без @
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithoutAt() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setEmail("aa?mm.ru");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с e-mail с двумя @
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithTwoAt() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setEmail("aa@mm@zz.ru");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с e-mail без доменной части
    //эндпоинт PUT /users
    @Test
    void updateWithEmailWithoutDomain() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setEmail("это-неправильный?эмейл@");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с пустым логином
    //эндпоинт PUT /users
    @Test
    void updateWithEmptyLogin() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setLogin("");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с логином с пробелом
    //эндпоинт PUT /users
    @Test
    void updateWithLoginWithSpace() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setLogin("a a");
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновить в контроллере объект с пустым именем
    //в поле имя должен подставиться логин
    //эндпоинт put /users
    @Test
    void updateWithEmptyName() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setName("");
        resultActions = mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user.setName(user.getLogin());
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    //обновить в контроллере объект с датой рождения в будущем
    //эндпоинт PUT /users
    @Test
    void updateWithBirthdayInFuture() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setBirthday(LocalDate.now().plusDays(2));
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //передать в контроллер указатель на null
    //эндпоинт PUT /users
    @Test
    void updateWithNull() throws Exception {
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //обновление с неверным id
    //эндпоинт PUT /users
    @Test
    void updateWithWrongId() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setId(-1);
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }

    //обновление с несуществующим id
    //эндпоинт PUT /users
    @Test
    void updateWithNotExistId() throws Exception {
        //создание
        User user = new User("aa@mm.ru", "a", "b", LocalDate.now().minusYears(18));
        ResultActions resultActions = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Integer id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "$.id");
        user.setId(id);
        resultActions.andExpect(content().json(objectMapper.writeValueAsString(user)));

        //обновление
        user.setId(2);
        mvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"))
                .andExpect(jsonPath("$.description").exists());
    }
}