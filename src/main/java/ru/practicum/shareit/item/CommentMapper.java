package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        Long id = commentDto.getId();
        String text = commentDto.getText();
        LocalDateTime created = commentDto.getCreated();
        return new Comment(id, text, created, item, author);
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                         .id(comment.getId())
                         .text(comment.getText())
                         .authorName(comment.getAuthor()
                                            .getName())
                         .created(comment.getCreated())
                         .build();
    }

}
