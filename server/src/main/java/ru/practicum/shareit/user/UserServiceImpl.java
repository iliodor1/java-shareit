package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        User oldUser = UserMapper.toUser(getUser(id));

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        return UserMapper.toDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> {
                                      log.error("User with id " + id + " not found!");
                                      return new NotFoundException(
                                              "User with id " + id + " not found!"
                                      );
                                  });
        return UserMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
    }

}
