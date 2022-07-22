package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto getUser(Long id);

    void deleteUser(Long id);

    List<UserDto> getAll();

}
