package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private BookingItemResponseDto lastBooking;
    private BookingItemResponseDto nextBooking;
    private final Long requestId;
    private List<CommentDto> comments;

}
