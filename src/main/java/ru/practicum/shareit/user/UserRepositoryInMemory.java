package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;

import javax.validation.ValidationException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryInMemory implements UserRepository {
    private long id;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (isValid(user) && !users.isEmpty()) {
            throw new ValidationException("User already exists with email: " + user.getEmail());
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
    public User updateUser(User user) {
        if (isValid(user) && !users.isEmpty()) {
            throw new ValidationException(user.getEmail() + ": this emile already exist!");
        }
        Long id = user.getId();

        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("User not exist with id: " + id);
        }

        User earlyUser = users.get(id);

        if (user.getEmail() != null) {
            earlyUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            earlyUser.setName(user.getName());
        }

        users.put(id, earlyUser);

        return earlyUser;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private void generateId() {
        ++id;
    }

    public boolean isValid(User user) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

}
