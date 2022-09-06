package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithStatusDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item, BookingStatus bookingStatus) {
        Long id = bookingDto.getId();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        return new Booking(id, start, end, item, booker, bookingStatus);
    }

    public static BookingWithStatusDto toBookingDto(Booking booking) {
        Long id = booking.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingStatus bookingStatus = booking.getStatus();
        User booker = booking.getBooker();
        UserDto bookerDto = UserDto.builder()
                                   .id(booker.getId())
                                   .name(booker.getName())
                                   .email(booker.getEmail())
                                   .build();
        Item item = booking.getItem();

        return BookingWithStatusDto.builder()
                                   .id(id)
                                   .start(start)
                                   .end(end)
                                   .status(bookingStatus)
                                   .booker(bookerDto)
                                   .item(ItemMapper.toInputDto(item))
                                   .build();
    }

}
