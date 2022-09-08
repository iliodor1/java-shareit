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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void whenGetByIdExist_thenReturnItemRequest() throws Exception {
        ItemRequestDto requestDto = createRequestDto(1L);

        when(service.getById(1L, 1L))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/" + requestDto.getId())
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
           .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    void whenGetOwnRequests_thenReturnListOfOwnRequests() throws Exception {
        List<ItemRequestDto> ownRequests = createRequestsDto();

        when(service.getOwnRequests(1L)).thenReturn(ownRequests);

        mvc.perform(get("/requests")
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(ownRequests.size()));
    }

    @Test
    void whenGetAllRequests_thenReturnListOfRequests() throws Exception {
        List<ItemRequestDto> requests = createRequestsDto();

        when(service.getAllRequests(1L, 0, 20)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(requests.size()));
    }

    @Test
    void whenCreateRequest_thenReturnItemRequest() throws Exception {
        ItemRequestDto requestDto = createRequestDto(1L);

        when(service.create(1L, requestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                   .content(mapper.writeValueAsString(requestDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
           .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    private ItemRequestDto createRequestDto(Long id) {
        return ItemRequestDto.builder()
                             .id(id)
                             .description("description")
                             .created(LocalDateTime.now())
                             .build();
    }

    private List<ItemRequestDto> createRequestsDto() {
        return List.of(
                createRequestDto(1L),
                createRequestDto(2L),
                createRequestDto(3L)
        );
    }

}
