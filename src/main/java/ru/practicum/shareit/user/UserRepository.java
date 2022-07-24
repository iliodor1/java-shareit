package ru.practicum.shareit.user;

import ru.practicum.shareit.user.mopel.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User addUser(User user);

    Optional<User> getUser(Long id);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getAll();

}
