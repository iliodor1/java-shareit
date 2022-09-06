package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingForItemOutputDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemOutputDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingForItemOutputDto lastBooking;
    BookingForItemOutputDto nextBooking;
    Long requestId;
    List<CommentDto> comments;
}
