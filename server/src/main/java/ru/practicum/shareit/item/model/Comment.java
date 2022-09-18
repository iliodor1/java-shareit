package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "text", nullable = false, length = 1000)
    String text;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;

}
