package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithStatusDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingWithStatusDto create(Long bookerId, BookingDto bookingDto);

    BookingWithStatusDto approve(Long owner, Long bookingId, boolean approved);

    BookingWithStatusDto getById(Long userId, Long bookingId);

    List<BookingWithStatusDto> getByBookerId(Long bookerId,
                                             BookingState bookingState,
                                             Integer from,
                                             Integer size);

    List<BookingWithStatusDto> getByOwnerId(Long ownerId,
                                            BookingState bookingState,
                                            Integer from,
                                            Integer size);

}
