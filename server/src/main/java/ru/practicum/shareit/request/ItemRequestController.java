package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("{requestId}")
    public ItemRequestDto getById(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.getById(requesterId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.create(requesterId, itemRequestDto);
    }

}
