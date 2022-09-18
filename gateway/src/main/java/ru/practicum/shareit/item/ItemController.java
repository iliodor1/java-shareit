package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.Marker.OnCreate;
import ru.practicum.shareit.validator.Marker.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "20")
            @Positive Integer size
    ) {
        return itemClient.getOwnItems(ownerId, from, size);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("{id}")
    @Validated(OnUpdate.class)
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long id,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemClient.update(ownerId, id, itemDto);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "20")
            @Positive Integer size
    ) {
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return itemClient.createComment(bookerId, itemId, commentDto);
    }

}
