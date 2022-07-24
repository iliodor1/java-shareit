package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto getItem(Long userId, Long id) {
        Item item = itemRepository.getItem(id)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", id);
                    return new NotFoundException("Item not found with id: " + id);
                });

        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getOwnItems(Long userId) {
        List<Item> items = itemRepository.getOwnItems(userId);

        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userId);

        return itemMapper.toDto(itemRepository.addItem(userId, item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userId);
        item.setId(itemId);

        return itemMapper.toDto(itemRepository.update(itemId, item));
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        if (text.isBlank()) return List.of();

        List<Item> items = itemRepository.searchItem(userId, text);

        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

}
