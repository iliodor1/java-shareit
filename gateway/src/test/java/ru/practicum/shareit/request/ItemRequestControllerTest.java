package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient client;

    @Autowired
    private MockMvc mvc;

    @Test
    void whenGetAllRequests_thenReturnOkStatus() throws Exception {
        mvc.perform(get("/requests/all")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetAllRequestsWithNegativeFromParam_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "-1")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAllRequestsWithNegativeSize_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", "-1"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAllRequestsWithZeroSize_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", "0"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateRequest_thenReturnItemRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                                                  .id(1L)
                                                  .description("description")
                                                  .created(LocalDateTime.now())
                                                  .build();

        mvc.perform(post("/requests")
                   .content(mapper.writeValueAsString(requestDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void whenCreateRequestWithNullDescription_thenReturnBadRequest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                                                  .id(1L)
                                                  .created(LocalDateTime.now())
                                                  .build();

        mvc.perform(post("/requests")
                   .content(mapper.writeValueAsString(requestDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

}
