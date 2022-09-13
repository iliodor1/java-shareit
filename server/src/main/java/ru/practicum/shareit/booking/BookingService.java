package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto create(Long bookerId, BookingRequestDto bookingRequestDto);

    BookingDto approve(Long owner, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByBookerId(Long bookerId,
                                             BookingState bookingState,
                                             Integer from,
                                             Integer size);

    List<BookingDto> getByOwnerId(Long ownerId,
                                            BookingState bookingState,
                                            Integer from,
                                            Integer size);

}
