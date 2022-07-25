package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mopel.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public User toUser(UserDto userDto) {
        Long id = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

}
