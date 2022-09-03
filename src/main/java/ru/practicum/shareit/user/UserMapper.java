package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return UserDto.builder()
                      .id(id)
                      .name(name)
                      .email(email)
                      .build();
    }

    public static User toUser(UserDto userDto) {
        Long id = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        return new User(id, name, email);
    }

}
