package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient client;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
                                           .id(1L)
                                           .name("item")
                                           .description("description")
                                           .available(true)
                                           .build();

    @Test
    void whenGetItemExist_thenReturnItemOkStatus() throws Exception {
        mvc.perform(get("/items/" + itemDto.getId())
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetOwnItems_thenReturnOkStatus() throws Exception {

        mvc.perform(get("/items")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "0")
                   .param("size", "20"))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetOwnItemsWithNegativeFrom_thenReturnBadRequest() throws Exception {

        mvc.perform(get("/items")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "-1")
                   .param("size", "20"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOwnItemsWithNegativeSize_thenReturnBadRequest() throws Exception {

        mvc.perform(get("/items")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", "-1"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOwnItemsWithZeroSize_thenReturnBadRequest() throws Exception {

        mvc.perform(get("/items")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "")
                   .param("size", "0"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateItem_thenReturnOkStatus() throws Exception {
        mvc.perform(post("/items")
                   .content(mapper.writeValueAsString(itemDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void whenCreateItemWithBlankName_thenReturnBadRequest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                                 .id(2L)
                                 .name(" ")
                                 .description("description")
                                 .available(true)
                                 .build();

        mvc.perform(post("/items")
                   .content(mapper.writeValueAsString(itemDto))
                   .header("X-Sharer-User-Id", itemDto.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateItemWithNullDescription_thenReturnBadRequest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                                 .id(2L)
                                 .name("name")
                                 .available(true)
                                 .build();

        mvc.perform(post("/items")
                   .content(mapper.writeValueAsString(itemDto))
                   .header("X-Sharer-User-Id", itemDto.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateItem_thenReturnUpdatedItemStatus200() throws Exception {

        mvc.perform(patch("/items/" + itemDto.getId())
                   .content(mapper.writeValueAsString(itemDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void whenUpdateItemBlankDescription_thenReturnBadRequest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                                 .id(2L)
                                 .name("name")
                                 .description(" ")
                                 .available(true)
                                 .build();

        mvc.perform(patch("/items/" + itemDto.getId())
                   .content(mapper.writeValueAsString(itemDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenSearchItem_thenReturnOkStatus() throws Exception {
        mvc.perform(get("/items/search")
                   .header("X-Sharer-User-Id", 1L)
                   .param("text", "item")
                   .param("from", "0")
                   .param("size", "20"))
           .andExpect(status().isOk());
    }

    @Test
    void whenSearchItemWithoutFromAndSize_thenReturnOkStatus() throws Exception {
        mvc.perform(get("/items/search")
                   .header("X-Sharer-User-Id", 1L)
                   .param("text", "item")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk());
    }

    @Test
    void whenSearchItemNegativeFromParam_thenReturnStatus4xx() throws Exception {
        mvc.perform(get("/items/search")
                   .header("X-Sharer-User-Id", 1L)
                   .param("text", "item")
                   .param("from", "-4")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateComment_thenReturnOkStatus() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .text("text")
                                          .authorName("author")
                                          .build();

        mvc.perform(post("/items/" + itemDto.getId() + "/comment")
                   .content(mapper.writeValueAsString(commentDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

}
