package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryInMemory implements ItemRepository {
    private long id;
    private final Map<Long, Item> items = new HashMap<>();
    private final UserRepository userRepository;

    public ItemRepositoryInMemory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Item> getItem(Long id) {
        return Optional.of(items.get(id));
    }

    @Override
    public Item create(Long ownerId, Item item) {
        if (userNotExist(ownerId)) {
            log.error("User with id: {} not exist!", ownerId);
            throw new NotFoundException("User with id: " + ownerId + " not exist!");
        }
        if (!item.getAvailable()) {
            log.error("Item {} has the availability status false", item.getName());
            throw new BadRequestException("Available status cannot be false");
        }
        generateId();
        item.setId(id);
        items.put(id, item);

        return items.get(id);
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            log.error("Item with id {} not exist!", itemId);
            throw new NotFoundException("Item not found with id" + itemId);
        }

        Item existingItem = items.get(itemId);

        if (!existingItem.getOwner().getId()
                         .equals(item.getOwner().getId())) {
            log.error("User with id {} is trying to update not own item with id {}",
                    item.getOwner().getId(), itemId);
            throw new NotFoundException(String.format(
                    "Item with id %s has not been added user id %s",
                    item.getId(), item.getOwner().getId()
            ));
        }
        if (item.getAvailable() != null
                && item.getAvailable() != existingItem.getAvailable()) {
            existingItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }

        items.put(itemId, existingItem);

        return items.get(itemId);
    }

    @Override
    public List<Item> getOwnItems(Long userId) {
        return items.values()
                    .stream()
                    .filter(i -> i.getOwner().getId().equals(userId))
                    .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values()
                    .stream()
                    .filter(i -> i.getAvailable().equals(true)
                            && (i.getName().toLowerCase().contains(text.toLowerCase())
                            || i.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .collect(Collectors.toList());
    }

    private void generateId() {
        ++id;
    }

    private boolean userNotExist(Long userId) {
        return userRepository.getAll()
                             .stream()
                             .noneMatch(u -> u.getId().equals(userId));
    }

}
