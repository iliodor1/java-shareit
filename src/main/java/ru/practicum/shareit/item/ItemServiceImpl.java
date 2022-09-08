package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto getItem(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> {
                                      log.error("Item with id {} not found", itemId);
                                      return new NotFoundException("Item not found with id: " + itemId);
                                  });

        return getItemOutputDto(item, ownerId);
    }

    @Override
    public List<ItemResponseDto> getOwnItems(Long ownerId, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId,
                                                 PageRequest.of(page, size))
                                         .toList();

        return items.stream()
                    .map(i -> getItemOutputDto(i, ownerId))
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        UserDto ownerDto = userService.getUser(ownerId);
        User owner = UserMapper.toUser(ownerDto);
        Optional<ItemRequest> itemRequest = itemDto.getRequestId() == null ? Optional.empty() :
                itemRequestRepository.findById(itemDto.getRequestId());

        Item item = ItemMapper.toItem(itemDto, owner);

        itemRequest.ifPresent(item::setRequest);

        return ItemMapper.toInputDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                                     .orElseThrow(() -> {
                                         log.error("Item with id {} not found or user isn't owner", itemId);
                                         return new NotFoundException(
                                                 "Item with id " + itemId + " not found or user isn't owner"
                                         );
                                     });

        Item newItem = ItemMapper.toItem(itemDto, null);

        String name = newItem.getName();
        String description = newItem.getDescription();
        Boolean available = newItem.getAvailable();

        if (name != null) {
            oldItem.setName(name);
        }
        if (description != null) {
            oldItem.setDescription(description);
        }
        if (available != null) {
            oldItem.setAvailable(available);
        }

        return ItemMapper.toInputDto(itemRepository.save(oldItem));
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        int page = from < size ? 0 : from / size;

        if (text.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.searchItem(text, PageRequest.of(page, size));

        return items.stream()
                    .map(ItemMapper::toInputDto)
                    .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long bookerId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository.findByBookerIdAndPastState(bookerId)
                                           .orElseThrow(() -> {
                                               log.error("Item {} booked by User {} was not found",
                                                       itemId, bookerId);
                                               return new BadRequestException(
                                                       String.format(
                                                               "Item %s booked by User %s was not found",
                                                               itemId, bookerId)
                                               );
                                           });

        Comment comment = CommentMapper.toComment(commentDto, booking.getItem(), booking.getBooker());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    private ItemResponseDto getItemOutputDto(Item item, Long ownerId) {
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), ownerId)
                                               .orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), ownerId)
                                               .orElse(null);

        ItemResponseDto itemResponseDto = ItemMapper.toOutputDto(item);

        BookingItemResponseDto lastBookingItemResponseDto;
        BookingItemResponseDto nextBookingItemResponseDto;

        if (lastBooking != null) {
            lastBookingItemResponseDto = new BookingItemResponseDto(lastBooking.getId(),
                    lastBooking.getBooker()
                               .getId());
            itemResponseDto.setLastBooking(lastBookingItemResponseDto);
        }
        if (nextBooking != null) {
            nextBookingItemResponseDto = new BookingItemResponseDto(nextBooking.getId(),
                    nextBooking.getBooker()
                               .getId());
            itemResponseDto.setNextBooking(nextBookingItemResponseDto);
        }

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        List<CommentDto> commentsDto = comments.stream()
                                               .map(CommentMapper::toCommentDto)
                                               .collect(Collectors.toList());
        itemResponseDto.setComments(commentsDto);

        return itemResponseDto;
    }

}
