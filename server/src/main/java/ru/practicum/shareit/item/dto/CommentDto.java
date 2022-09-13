package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto implements Serializable {
    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;

}
