package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private final Long id;
    private final Long itemId;
    private final LocalDateTime start;
    private final LocalDateTime end;

}
