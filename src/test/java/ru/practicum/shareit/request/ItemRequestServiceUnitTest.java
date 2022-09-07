package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserServiceImpl userService;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void whenGetItemRequestById_thenCallItemRequestRepositoryFindById() {
        when(userService.getUser(anyLong()))
                .thenReturn(createUserDto(1L));

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest(1L)));

        itemRequestService.getById(1L, 1L);

        verify(itemRequestRepository, times(1)).findById(anyLong());

    }

    @Test
    void whenGetItemRequestById_thenReturnItemRequest() {
        when(userService.getUser(anyLong()))
                .thenReturn(createUserDto(1L));

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest(1L)));

        ItemRequestDto itemRequestDto = itemRequestService.getById(1L, 1L);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    void whenGetByIdNotExistItemRequest_thenThrowNotFoundException() {
        when(userService.getUser(anyLong()))
                .thenReturn(createUserDto(1L));

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> itemRequestService.getById(1L, 1L)
        );

        assertEquals("Item request with id 1 not exist", exception.getMessage());
    }


    @Test
    void whenCreateItemRequest_thenCallItemRequestRepositorySave() {
        when(userService.getUser(anyLong())).thenReturn(createUserDto(1L));
        when(itemRequestRepository.save(any())).thenReturn(createItemRequest(1L));

        itemRequestService.create(1L, createItemRequestDto(1L));

        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void whenCreateItemRequest_thenReturnItemRequest() {
        when(userService.getUser(anyLong())).thenReturn(createUserDto(1L));
        when(itemRequestRepository.save(any())).thenReturn(createItemRequest(1L));

        ItemRequestDto itemRequestDto = itemRequestService.create(1L, createItemRequestDto(1L));

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
    }

    @Test
    void whenGetOwnRequests_thenReturnListItemRequestsDto() {
        when(userService.getUser(anyLong())).thenReturn(createUserDto(1L));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(any()))
                .thenReturn(List.of(createItemRequest(1L),
                        createItemRequest(2L),
                        createItemRequest(3L)));

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getOwnRequests(1L);

        assertEquals(3, itemRequestsDto.size());
    }

    @Test
    void whenGetOwnRequests_thenCallItemRequestsRepository() {
        when(userService.getUser(anyLong())).thenReturn(createUserDto(1L));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(any()))
                .thenReturn(List.of(createItemRequest(1L),
                        createItemRequest(2L),
                        createItemRequest(3L)));

        itemRequestService.getOwnRequests(1L);

        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdOrderByCreated(any());
    }

    @Test
    void whenGetAllRequests_thenReturnListItemRequestsDto() {
        ItemRequest itemRequest1 = createItemRequest(1L);
        ItemRequest itemRequest2 = createItemRequest(2L);
        ItemRequest itemRequest3 = createItemRequest(3L);

        itemRequest1.setRequester(createUser(1L));
        itemRequest2.setRequester(createUser(2L));
        itemRequest3.setRequester(createUser(3L));

        when(userService.getUser(anyLong())).thenReturn(createUserDto(4L));

        when(itemRequestRepository.findAllWithoutUserRequests(
                        4L,
                        PageRequest.of(0, 20, Sort.by("created")
                                                  .descending())
                )
        )
                .thenReturn(new PageImpl<>(List.of(itemRequest1, itemRequest2, itemRequest3)));

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequests(4L, 0, 20);

        verify(itemRequestRepository, times(1))
                .findAllWithoutUserRequests(
                        4L,
                        PageRequest.of(0, 20, Sort.by("created")
                                                   .descending())
                );
        assertEquals(3, itemRequestsDto.size());
    }

    private UserDto createUserDto(Long id) {
        return new UserDto(id, "user" + id, "user" + id + "@mail.ru");
    }

    private User createUser(Long id) {
        return User.builder()
                   .id(id)
                   .name("user" + id)
                   .email("user" + id + "@email.ru")
                   .build();
    }

    private ItemRequest createItemRequest(Long id) {
        return new ItemRequest(id, "description", null, LocalDateTime.now());
    }

    private ItemRequestDto createItemRequestDto(Long id) {
        return new ItemRequestDto(id, "description", LocalDateTime.now(), List.of());
    }

}
