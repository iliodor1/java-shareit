package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemResponseDto getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getOwnItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return itemService.getOwnItems(ownerId, from, size);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long id,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.update(ownerId, id, itemDto);
    }

    @GetMapping("search")
    public List<ItemDto> searchItem(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0")
            Integer from,
            @RequestParam(required = false, defaultValue = "20")
            Integer size
    ) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        return itemService.createComment(bookerId, itemId, commentDto);
    }

}
