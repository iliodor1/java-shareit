package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getById(Long requesterId, Long requestId);

    ItemRequestDto create(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnRequests(Long requester);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

}
