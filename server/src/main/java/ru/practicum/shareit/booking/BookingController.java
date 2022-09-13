package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return bookingService.create(bookerId, bookingRequestDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam BookingState state,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        return bookingService.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping("owner")
    public List<BookingDto> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam BookingState state,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        return bookingService.getByOwnerId(ownerId, state, from, size);
    }

}
