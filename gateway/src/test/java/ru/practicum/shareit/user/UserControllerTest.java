package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient client;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com"
    );

    @Test
    void whenCreateUser_thenReturnUserStatus2xx() throws Exception {
        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(userDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
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
    void whenCreateUserWithBlankEmail_thenReturnStatus4xx() throws Exception {
        UserDto user = UserDto.builder()
                              .name("name")
                              .email(" ")
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
    void whenUpdateUserWithBlankEmail_thenReturnStatus4xx() throws Exception {
        UserDto userWithNewEmail = UserDto.builder()
                                          .email(" ")
                                          .build();

        mvc.perform(patch("/users/" + userDto.getId())
                   .content(mapper.writeValueAsString(userWithNewEmail))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateUserWithBlankName_thenReturnStatus4xx() throws Exception {
        UserDto userWithNewEmail = UserDto.builder()
                                          .name(" ")
                                          .build();

        mvc.perform(patch("/users/" + userDto.getId())
                   .content(mapper.writeValueAsString(userWithNewEmail))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetUserExist_thenReturnStatus2xx() throws Exception {
        mvc.perform(get("/users/" + userDto.getId()))
           .andExpect(status().isOk());
    }

    @Test
    void whenDelete_thenReturnStatus200() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId()))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetAllUsers_thenReturnStatus200() throws Exception {
        mvc.perform(get("/users"))
           .andExpect(status().isOk());
    }

}
