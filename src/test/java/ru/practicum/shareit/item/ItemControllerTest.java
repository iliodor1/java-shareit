package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    private final ItemInputDto itemInputDto = ItemInputDto.builder()
                                                          .id(1L)
                                                          .name("item")
                                                          .description("description")
                                                          .available(true)
                                                          .build();

    private final ItemOutputDto itemOutputDto = ItemOutputDto.builder()
                                                             .id(1L)
                                                             .name("item")
                                                             .description("description")
                                                             .available(true)
                                                             .build();

    @Test
    void whenGetItemExist_thenReturnItemStatus2xx() throws Exception {
        when(service.getItem(1L, itemInputDto.getId())).thenReturn(itemOutputDto);

        mvc.perform(get("/items/" + itemInputDto.getId())
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(itemOutputDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(itemOutputDto.getName())))
           .andExpect(jsonPath("$.description", is(itemOutputDto.getDescription())))
           .andExpect(jsonPath("$.available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void whenGetOwnItems_thenReturnLiatOfOwnItemsStatus2xx() throws Exception {
        List<ItemOutputDto> itemsDto = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            ItemOutputDto item = ItemOutputDto.builder()
                                              .id((long) i)
                                              .name("item" + i)
                                              .description("description")
                                              .available(true)
                                              .build();
            itemsDto.add(item);
        }

        when(service.getOwnItems(anyLong(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items")
                   .header("X-Sharer-User-Id", 1L)
                   .param("from", "0")
                   .param("size", "20"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

    @Test
    void whenCreateItem_thenReturnItemStatus200() throws Exception {
        when(service.create(1L, itemInputDto)).thenReturn(itemInputDto);

        mvc.perform(post("/items")
                   .content(mapper.writeValueAsString(itemInputDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(itemInputDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(itemInputDto.getName())))
           .andExpect(jsonPath("$.description", is(itemInputDto.getDescription())))
           .andExpect(jsonPath("$.available", is(itemInputDto.getAvailable())));
    }

    @Test
    void whenUpdateItem_thenReturnUpdatedItemStatus200() throws Exception {
        ItemInputDto newItemName = ItemInputDto.builder()
                                               .name("newName")
                                               .build();

        ItemInputDto updatedItem = ItemInputDto.builder()
                                               .id(itemInputDto.getId())
                                               .name(newItemName.getName())
                                               .description(itemInputDto.getDescription())
                                               .available(itemInputDto.getAvailable())
                                               .build();

        when(service.update(1L, itemInputDto.getId(), newItemName)).thenReturn(updatedItem);

        mvc.perform(patch("/items/" + itemInputDto.getId())
                   .content(mapper.writeValueAsString(newItemName))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(updatedItem.getName())))
           .andExpect(jsonPath("$.description", is(updatedItem.getDescription())))
           .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())));
    }

    @Test
    void whenSearchItem_thenReturnListOfItemsStatus2xx() throws Exception {
        List<ItemInputDto> itemsDto = createItemsInputDto();

        when(service.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                   .param("text", "item")
                   .param("from", "0")
                   .param("size", "20"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

    @Test
    void whenSearchItemWithoutFromAndSize_thenReturnListOfItemsWithDefaultFromAndSize() throws Exception {
        List<ItemInputDto> itemsDto = createItemsInputDto();

        when(service.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                   .param("text", "item")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

    @Test
    void whenSearchItemNegativeFromOrSizeParam_thenReturnStatus4xx() throws Exception {
        List<ItemInputDto> itemsDto = createItemsInputDto();

        when(service.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                   .param("text", "item")
                   .param("from", "-4")
                   .param("size", "-20"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .text("text")
                                          .authorName("author")
                                          .build();

        when(service.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/" + itemInputDto.getId() + "/comment")
                   .content(mapper.writeValueAsString(commentDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
           .andExpect(jsonPath("$.text", is(commentDto.getText())))
           .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    private List<ItemInputDto> createItemsInputDto() {
        List<ItemInputDto> itemsDto = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            ItemInputDto item = ItemInputDto.builder()
                                            .id((long) i)
                                            .name("item" + i)
                                            .description("description")
                                            .available(true)
                                            .build();
            itemsDto.add(item);
        }

        return itemsDto;
    }

}
