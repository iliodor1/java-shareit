package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(bookerId, bookingRequestDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                          @RequestParam(required = false,
                                                  defaultValue = "ALL") String state,
                                          @RequestParam(required = false, defaultValue = "0")
                                          @PositiveOrZero Integer from,
                                          @RequestParam(required = false, defaultValue = "20")
                                          @Positive Integer size) {
        return bookingService.getByBookerId(bookerId, getEnumState(state), from, size);
    }

    @GetMapping("owner")
    public List<BookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
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
