package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exeption.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return bookingClient.create(bookerId, bookingRequestDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(required = false,
                                                        defaultValue = "ALL") String state,
                                                @RequestParam(required = false, defaultValue = "0")
                                                @PositiveOrZero Integer from,
                                                @RequestParam(required = false, defaultValue = "20")
                                                @Positive Integer size) {
        return bookingClient.getByBookerId(bookerId, getEnumState(state), from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(required = false,
                                                       defaultValue = "ALL") String state,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "20")
                                               @Positive Integer size) {
        return bookingClient.getByOwnerId(ownerId, getEnumState(state), from, size);
    }

    private BookingState getEnumState(String state) {
        BookingState bookingStateEnum;
        try {
            bookingStateEnum = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error(state);
            throw new BadRequestException("Unknown state: " + state);
        }

        return bookingStateEnum;
    }

}
