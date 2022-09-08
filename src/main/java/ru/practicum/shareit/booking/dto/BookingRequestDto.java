package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import lombok.Builder;
import lombok.Data;

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
