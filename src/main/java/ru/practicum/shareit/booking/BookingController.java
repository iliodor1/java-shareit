package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exeption.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingStatusDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                   @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookerId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingStatusDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingStatusDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingStatusDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(required = false,
                                                        defaultValue = "ALL") String state,
                                                @RequestParam(required = false, defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                @RequestParam(required = false, defaultValue = "20")
                                                    @Positive Integer size) {
        return bookingService.getByBookerId(bookerId, getEnumState(state), from, size);
    }

    @GetMapping("owner")
    public List<BookingStatusDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(required = false,
                                                       defaultValue = "ALL") String state,
                                               @RequestParam(required = false, defaultValue = "0")
                                                   @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "20")
                                                   @Positive Integer size) {
        return bookingService.getByOwnerId(ownerId, getEnumState(state), from, size);
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
