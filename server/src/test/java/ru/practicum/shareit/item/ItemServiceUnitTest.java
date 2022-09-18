package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void whenGetItemById_thenReturnItemOutputDtoWithId1() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(createItem(1L)));

        when(bookingRepository.findLastBooking(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findNextBooking(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of());


        ItemResponseDto itemResponseDto = itemService.getItem(1L, anyLong());

        assertEquals(1L, itemResponseDto.getId());
    }

    @Test
    void whenGetItemById_thenCallItemRepositoryFindById() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(createItem(1L)));

        when(bookingRepository.findLastBooking(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findNextBooking(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of());

        itemService.getItem(1L, anyLong());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetItemById_thenThrowNotFoundException() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItem(1L, anyLong()));

        assertEquals("Item not found with id: 1", exception.getMessage());
    }


    @Test
    void whenGetOwnItems_thenReturnListItemOutputDto() {
        when(itemRepository.findAllByOwnerIdOrderById(1L, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(createItem(1L), createItem(2L))));

        List<ItemResponseDto> items = itemService.getOwnItems(1L, 0, 20);

        assertEquals(2, items.size());
    }

    @Test
    void whenCreate_thenCallItemRepositorySave() {
        when(userService.getUser(anyLong()))
                .thenReturn(new UserDto(1L, "name", "email@mail.ru"));
        when(itemRepository.save(any())).thenReturn(createItem(1L));

        itemService.create(anyLong(), createItemDto(1L));

        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void whenCreateItemId1_thenReturnItemInputDtoId1() {
        when(userService.getUser(anyLong()))
                .thenReturn(new UserDto(1L, "name", "email@mail.ru"));
        when(itemRepository.save(any())).thenReturn(createItem(1L));

        ItemDto itemDto = itemService.create(anyLong(), createItemDto(1L));

        assertEquals(1L, itemDto.getId());
    }

    @Test
    void whenUpdateItem_thenCallItemRepositorySave() {
        Item item = createItem(1L);

        when(itemRepository.findByIdAndOwnerId(1L, 1L))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(new Item(1L,
                        "UpdatedItem",
                        "Description1",
                        true,
                        null));

        itemService.update(1L, 1L, new ItemDto(null,
                "UpdatedItem",
                null,
                true,
                null));

        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void whenUpdateItemName_thenReturnUpdatedItemName() {
        Item item = createItem(1L);

        when(itemRepository.findByIdAndOwnerId(1L, 1L))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(new Item(1L,
                        "UpdatedItem",
                        "Description1",
                        true,
                        null));

        ItemDto itemDto = itemService.update(
                1L,
                1L,
                new ItemDto(
                        null,
                        "UpdatedItem",
                        null,
                        null,
                        null
                )
        );

        assertEquals("UpdatedItem", itemDto.getName());
        assertEquals(1L, itemDto.getId());
        assertEquals("Description1", itemDto.getDescription());
    }

    @Test
    void whenUpdateItemDescription_thenReturnUpdatedItemDescription() {
        Item item = createItem(1L);

        when(itemRepository.findByIdAndOwnerId(1L, 1L))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(new Item(1L,
                        "Item1",
                        "UpdatedDescription",
                        null,
                        null));

        ItemDto itemInputDto =
                itemService.update(
                        1L,
                        1L,
                        new ItemDto(null,
                                null,
                                "UpdatedDescription",
                                null,
                                null
                        )
                );

        assertEquals("UpdatedDescription", itemInputDto.getDescription());
        assertEquals("Item1", itemInputDto.getName());
        assertEquals(1L, itemInputDto.getId());
    }

    @Test
    void whenUpdateItemAvailable_thenReturnUpdatedItemAvailable() {
        Item item = createItem(1L);

        when(itemRepository.findByIdAndOwnerId(1L, 1L))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(new Item(1L,
                        "Item1",
                        "Description1",
                        false,
                        null));

        ItemDto itemDto = itemService.update(
                1L,
                1L,
                new ItemDto(
                        null,
                        null,
                        null,
                        false,
                        null
                )
        );

        assertFalse(itemDto.getAvailable());
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId());
        assertEquals("Description1", itemDto.getDescription());
    }

    @Test
    void whenSearchItem_thenCallItemRepositorySearchItem() {
        List<Item> items = List.of(createItem(1L), createItem(2L), createItem(3L));

        when(itemRepository.searchItem("Item", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(items).toList());

        itemService.searchItem("Item", 0, 20);

        verify(itemRepository, times(1))
                .searchItem("Item", PageRequest.of(0, 20));
    }

    @Test
    void whenSearchItemExist_thenReturnListOfItems() {
        List<Item> items = List.of(createItem(1L), createItem(2L), createItem(3L));

        when(itemRepository.searchItem("Item", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(items).toList());

        List<ItemDto> itemsInputDto = itemService.searchItem("Item", 0, 20);

        assertEquals(3, itemsInputDto.size());
    }

    @Test
    void whenCreateComment_thenCallCommentRepositorySave() {
        User user1 = createUser(1L);
        Item item1 = createItem(1L);

        Booking booking = new Booking(1L,
                LocalDateTime.now()
                             .minusHours(2),
                LocalDateTime.now()
                             .minusHours(1),
                item1,
                user1,
                BookingStatus.WAITING);

        Comment comment = createComment(1L, item1, user1);
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .text("textComment1")
                                          .authorName("user1")
                                          .created(LocalDateTime.now())
                                          .build();

        when(bookingRepository.findByBookerIdAndPastState(anyLong())).thenReturn(Optional.of(booking));

        when(commentRepository.save(any()))
                .thenReturn(comment);

        itemService.createComment(1L, 1L, commentDto);

        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void whenCreateComment_thenReturnComment() {
        User user1 = createUser(1L);
        Item item1 = createItem(1L);

        Booking booking = new Booking(1L,
                LocalDateTime.now()
                             .minusHours(2),
                LocalDateTime.now()
                             .minusHours(1),
                item1,
                user1,
                BookingStatus.WAITING);

        Comment comment = createComment(1L, item1, user1);
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .text("textComment1")
                                          .authorName("user1")
                                          .created(LocalDateTime.now())
                                          .build();

        when(bookingRepository.findByBookerIdAndPastState(anyLong())).thenReturn(Optional.of(booking));

        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentOutputDto = itemService.createComment(1L, 1L, commentDto);

        assertEquals(1L, commentOutputDto.getId());
        assertEquals("textComment1", commentOutputDto.getText());
        assertEquals("user1", commentOutputDto.getAuthorName());
        assertEquals(LocalDateTime.now()
                                  .truncatedTo(ChronoUnit.SECONDS)
                                  .format(DateTimeFormatter.ISO_DATE_TIME),
                commentOutputDto.getCreated()
                                .truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ISO_DATE_TIME));
    }

    private Item createItem(Long id) {
        return new Item(
                id,
                "Item" + id,
                "Description" + id,
                true,
                null
        );
    }

    private ItemDto createItemDto(Long id) {
        return new ItemDto(
                id,
                "Item" + id,
                "Description" + id,
                true,
                null);
    }

    private Comment createComment(Long id, Item item, User user) {
        return new Comment(
                id,
                "textComment" + id,
                LocalDateTime.now(),
                item,
                user
        );
    }

    private User createUser(Long id) {
        return User.builder()
                   .id(id)
                   .name("user" + id)
                   .email("user" + id + "@email.ru")
                   .build();
    }

}
