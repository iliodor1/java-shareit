package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {

    private final Long id;
    private final Long itemId;
    @Future
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;

}
