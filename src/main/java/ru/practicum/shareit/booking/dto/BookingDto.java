package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class BookingDto {
    private final Long id;
    @Future
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;
    private final BookingStatus status;
    private final UserDto booker;
    private final ItemDto item;

}
