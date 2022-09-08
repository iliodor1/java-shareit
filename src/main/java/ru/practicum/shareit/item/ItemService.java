package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemResponseDto getItem(Long id, Long userId);

    List<ItemResponseDto> getOwnItems(Long ownerId, Integer from, Integer size);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    CommentDto createComment(Long bookerId, Long itemId, CommentDto commentDto);

}
