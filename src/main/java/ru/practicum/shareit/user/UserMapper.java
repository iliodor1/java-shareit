package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        Long id = user.getId();

        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
