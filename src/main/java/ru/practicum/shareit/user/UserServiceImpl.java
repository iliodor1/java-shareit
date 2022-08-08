package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mopel.User;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        return userMapper.toDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setId(id);

        return userMapper.toDto(userRepository.update(user));
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.getUser(id)
                                  .orElseThrow(() -> {
                                      log.error("User with id " + id + " not found!");
                                      return new NotFoundException("User with id " + id + " not found!");
                                  });
        return userMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.getAll();

        return users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
    }

}
