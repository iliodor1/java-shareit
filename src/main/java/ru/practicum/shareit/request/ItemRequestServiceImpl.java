package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getById(Long requesterId, Long requestId) {
        userService.getUser(requesterId);

        ItemRequest itemRequest =
                itemRequestRepository.findById(requestId)
                                     .orElseThrow(() -> {
                                         log.error("Item request with id " + requestId + " not exist");
                                         return new NotFoundException(
                                                 "Item request with id " + requestId + " not exist"
                                         );
                                     });

        return ItemRequestMapper.toDto(itemRequest, getItemsByRequestId(requestId));
    }

    @Override
    public ItemRequestDto create(Long requesterId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreated(LocalDateTime.now());
        User requester = UserMapper.toUser(userService.getUser(requesterId));

        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequestMapper.toItem(itemRequestDto, requester)
        );

        return ItemRequestMapper.toDto(itemRequest, getItemsByRequestId(itemRequest.getId()));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long requesterId) {
        UserMapper.toUser(userService.getUser(requesterId));

        return itemRequestRepository.findAllByRequesterIdOrderByCreated(requesterId)
                                    .stream()
                                    .map(r -> ItemRequestMapper.toDto(r, getItemsByRequestId(r.getId())))
                                    .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        UserMapper.toUser(userService.getUser(userId));

        int page = from < size ? 0 : from / size;

        Page<ItemRequest> requests =
                itemRequestRepository.findAllWithoutUserRequests(
                        userId,
                        PageRequest.of(page, size, Sort.by("created").descending())
                );

        return requests.stream()
                       .map(r -> ItemRequestMapper.toDto(r, getItemsByRequestId(r.getId())))
                       .collect(Collectors.toList());
    }

    private List<ItemResponseDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAllItemsByRequestId(requestId);

        return items.stream()
                    .map(ItemMapper::toOutputDto)
                    .collect(Collectors.toList());
    }

}
