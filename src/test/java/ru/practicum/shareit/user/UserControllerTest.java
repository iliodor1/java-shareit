package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com"
    );

    @Test
    void whenCreateUser_thenReturnUserStatus2xx() throws Exception {
        when(service.create(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(userDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(userDto.getName())))
           .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void whenCreateUserWithExistEmail_thenReturnStatus4xx() throws Exception {
        when(service.create(userDto)).thenThrow(BadRequestException.class);

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(userDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    @Test
    void whenCreateUserWithNullEmail_thenReturnStatus4xx() throws Exception {
        UserDto user = UserDto.builder()
                              .name("name")
                              .build();

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }


    @Test
    void whenCreateUserWithBlankName_thenReturnStatus4xx() throws Exception {
        UserDto user = UserDto.builder()
                              .name(" ")
                              .email("email@email.ru")
                              .build();

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateUserWithInvalidEmail_thenReturnStatus4xx() throws Exception {
        UserDto user = UserDto.builder()
                              .name("name")
                              .email("email")
                              .build();

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    @Test
    void whenGetUserExist_thenReturnUserDtoStatus2xx() throws Exception {
        when(service.getUser(userDto.getId())).thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(userDto.getName())))
           .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void whenGetUserNotExist_thenReturnStatus404() throws Exception {
        when(service.getUser(-1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/-1"))
           .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateEmail_thenReturnUserWithUpdatedEmailAndStatus200() throws Exception {
        UserDto userWithNewEmail = UserDto.builder()
                                          .email("updated@mail.ru")
                                          .build();

        UserDto updatedUserDto = UserDto.builder()
                                        .id(userDto.getId())
                                        .name(userDto.getName())
                                        .email(userWithNewEmail.getEmail())
                                        .build();

        when(service.update(userDto.getId(), userWithNewEmail))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/" + userDto.getId())
                   .content(mapper.writeValueAsString(userWithNewEmail))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
           .andExpect(jsonPath("$.email", is(userWithNewEmail.getEmail())));
    }

    @Test
    void whenDelete_thenReturnStatus200() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId()))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetAllUsers_thenReturnUsersListStatus200() throws Exception {
        List<UserDto> users = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            UserDto user = UserDto.builder()
                                  .id((long) i)
                                  .name("user" + i)
                                  .email(i + "mail@mail.ru")
                                  .build();
            users.add(user);
        }

        when(service.getAll()).thenReturn(users);

        mvc.perform(get("/users"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(users.size()));
    }

}
