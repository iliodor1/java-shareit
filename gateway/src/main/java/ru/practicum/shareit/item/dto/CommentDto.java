package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto implements Serializable {
    private final Long id;
    @Size(min = 1, max = 1000)
    private final String text;
    private final String authorName;
    private final LocalDateTime created;

}
