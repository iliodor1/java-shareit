package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest itemRequest, List<ItemOutputDto> items) {
        Long id = itemRequest.getId();
        String description = itemRequest.getDescription();
        LocalDateTime created = itemRequest.getCreated();

        return new ItemRequestDto(id, description, created, items);
    }

    public ItemRequest toItem(ItemRequestDto itemRequestDto, User requester) {
        String description = itemRequestDto.getDescription();
        LocalDateTime created = itemRequestDto.getCreated();

        return new ItemRequest(description, requester, created);
    }

}

