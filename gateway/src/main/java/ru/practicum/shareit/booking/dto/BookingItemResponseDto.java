package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingItemResponseDto {
    private final Long id;
    private final Long bookerId;

}
