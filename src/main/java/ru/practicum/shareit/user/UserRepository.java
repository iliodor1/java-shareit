package ru.practicum.shareit.user;

import ru.practicum.shareit.user.mopel.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> getUser(Long id);

    User update(User user);

    void delete(Long id);

    List<User> getAll();

}
