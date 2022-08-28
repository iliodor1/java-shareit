package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemOutputDto;
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
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getById(Long requesterId, Long requestId) {
        userService.getUser(requesterId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                                                       .orElseThrow(() -> new NotFoundException(
                                                               "Item request with id " + requestId + " not exist"
                                                       ));

        return itemRequestMapper.toDto(itemRequest, getItemsByRequestId(requestId));
    }

    @Override
    public ItemRequestDto create(Long requesterId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreated(LocalDateTime.now());
        User requester = userMapper.toUser(userService.getUser(requesterId));

        ItemRequest itemRequest = itemRequestRepository.save(
                itemRequestMapper.toItem(itemRequestDto, requester)
        );

        return itemRequestMapper.toDto(itemRequest, getItemsByRequestId(itemRequest.getId()));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long requesterId) {
        userMapper.toUser(userService.getUser(requesterId));

        return itemRequestRepository.findAllByRequesterIdOrderByCreated(requesterId)
                                    .stream()
                                    .map(r -> itemRequestMapper.toDto(r, getItemsByRequestId(r.getId())))
                                    .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userMapper.toUser(userService.getUser(userId));

        Page<ItemRequest> requests = itemRequestRepository.findAll(PageRequest.of(from, size));

        return requests.stream().filter(r-> !(r.getRequester().getId().equals(userId)))
                       .map(r -> itemRequestMapper.toDto(r, getItemsByRequestId(r.getId())))
                       .collect(Collectors.toList());
    }

    private List<ItemOutputDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAllItemsByRequestId(requestId);

        return items.stream()
                    .filter(i -> i.getRequest()
                                  .getId()
                                  .equals(requestId))
                    .map(itemMapper::toOutputDto)
                    .collect(Collectors.toList());
    }

}
