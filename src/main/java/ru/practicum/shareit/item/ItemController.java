package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.validator.Marker.OnCreate;
import ru.practicum.shareit.validator.Marker.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getOwnItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(required = false, defaultValue = "0")
                                           @PositiveOrZero Integer from,
                                             @RequestParam(required = false, defaultValue = "20")
                                           @Positive Integer size) {
        return itemService.getOwnItems(ownerId, from, size);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("{id}")
    @Validated(OnUpdate.class)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long id,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, id, itemDto);
    }

    @GetMapping("search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(required = false, defaultValue = "0")
                                    @PositiveOrZero Integer from,
                                    @RequestParam(required = false, defaultValue = "20")
                                    @Positive Integer size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(bookerId, itemId, commentDto);
    }

}
