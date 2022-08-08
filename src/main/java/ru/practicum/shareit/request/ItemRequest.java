package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;
    String description;
    Long requestorId;
    LocalDateTime created;

}
