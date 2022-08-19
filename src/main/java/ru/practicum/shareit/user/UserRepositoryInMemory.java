package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ConflictRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.mopel.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryInMemory implements UserRepository {
    private long id;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (isNotUniqueEmail(user)) {
            log.error("User with email " + user.getEmail() + " already exists!");
            throw new ConflictRequestException(
                    "User with email " + user.getEmail() + " already exists!"
            );
        }

        generateId();
        user.setId(id);
        users.put(id, user);

        return users.get(id);
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        users.computeIfAbsent(id, v -> {
                    log.error("User with id " + id + " not found!");
                    throw new NotFoundException("User with id " + id + " not found!");
                }
        );

        if (isNotUniqueEmail(user)) {
            log.error("User with email " + user.getEmail() + " already exists!");
            throw new ConflictRequestException(
                    "User with email " + user.getEmail() + " already exists!"
            );
        }

        User currentUser = users.get(id);

        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        users.put(id, currentUser);

        return currentUser;
    }

    @Override
    public void delete(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            log.error("User with id " + id + " not found!");
            throw new NotFoundException("User with id " + id + " not found!");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private void generateId() {
        ++id;
    }

    private boolean isNotUniqueEmail(User user) {
        return users.values()
                    .stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

}
