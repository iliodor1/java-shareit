package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.Marker.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{id}")
    public ItemDto getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getOwnItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getOwnItems(ownerId);
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
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

}
