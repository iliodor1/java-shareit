package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class BookingMapper {

    public Booking toBooking(BookingDto bookingDto, User booker, Item item, BookingStatus bookingStatus) {
        Long id = bookingDto.getId();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        return new Booking(id, start, end, item, booker, bookingStatus);
    }

    public BookingStatusDto toBookingDto(Booking booking) {
        Long id = booking.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingStatus bookingStatus = booking.getStatus();
        User booker = booking.getBooker();
        UserDto bookerDto = new UserDto(booker.getId(), booker.getName(), booker.getEmail());
        Item item = booking.getItem();
        ItemInputDto itemInputDto = new ItemInputDto(item.getId(), item.getName(),
                item.getDescription(),item.getAvailable());

        return new BookingStatusDto(id, start, end, bookingStatus, bookerDto, itemInputDto);
    }

}
