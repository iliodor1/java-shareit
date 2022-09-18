package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    public void whenGetExistingBookingById_thenCallBookingRepositoryFindById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(
                        createBooking(1L, createItem(1L, createUser(2L)), createUser(1L))
                ));

        bookingService.getById(1L, 1L);

        verify(bookingRepository, times(1)).findById(anyLong());
    }


    @Test
    public void whenTryGetExistingBookingById_thenReturnModel() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(
                        createBooking(1L, createItem(1L, createUser(2L)), createUser(1L))
                ));

        BookingDto booking = bookingService.getById(1L, 1L);

        assertEquals(1L, booking.getId());
    }

    @Test
    public void whenTryGetNotExistingBookingById_thenThrowNotFoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(1L, -99L));

        assertEquals("Booking not found with id: -99", exception.getMessage());
    }

    @Test
    public void whenTryGetNotOwnerOrBookerExistingBookingById_thenThrowNotFoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(
                        createBooking(1L, createItem(1L, createUser(2L)), createUser(1L))
                ));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(-99L, 1L));

        assertEquals("Only the item owner or the booker can getById the booking", exception.getMessage());
    }

    @Test
    void whenCreateBooking_thenCallBookingRepositorySave() {
        User booker = createUser(1L);
        Item item = createItem(1L, createUser(2L));
        Booking booking = createBooking(1L, item, booker);
        BookingRequestDto bookingDto = createBookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.create(1L, bookingDto);

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void whenCreateBooking_thenReturnBooking() {
        User booker = createUser(1L);
        Item item = createItem(1L, createUser(2L));
        Booking booking = createBooking(1L, item, booker);
        BookingRequestDto bookingRequestDto = createBookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.create(1L, bookingRequestDto);

        assertEquals(1L, bookingDto.getId());
        assertEquals("user1", bookingDto.getBooker()
                                                  .getName());
        assertEquals("Item1", bookingDto.getItem()
                                                  .getName());
    }

    @Test
    void whenCreateBookingWhereBookerNotExist_thenThrowNotFoundException() {
        BookingRequestDto bookingDto = createBookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(-1L, bookingDto));

        assertEquals("User with id -1 not found!", exception.getMessage());
    }

    @Test
    void whenCreateBookingWhereItemNotExist_thenThrowNotFoundException() {
        User booker = createUser(1L);
        BookingRequestDto bookingDto = createBookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDto));

        assertEquals("Item not found with id: 1", exception.getMessage());
    }

    @Test
    void whenCreateBookingOwnItem_thenThrowNotFoundException() {
        User booker = createUser(1L);
        Item item = createItem(1L, booker);
        BookingRequestDto bookingDto = createBookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDto));

        assertEquals("User can't booking own item", exception.getMessage());
    }

    @Test
    void whenCreateBookingNotAvailableItem_thenThrowBadRequestException() {
        User booker = createUser(1L);
        Item item = createItem(1L, createUser(2L));
        BookingRequestDto bookingDto = createBookingDto();

        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.create(1L, bookingDto));

        assertEquals("The item is not available!", exception.getMessage());
    }

    @Test
    void whenApprove_thenCallBookingRepositorySave() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.approve(2L, 1L, true);

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void whenApprove_thenReturnApproveBooking() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto =
                bookingService.approve(2L, 1L, true);

        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void whenApproveApprovedBooking_thenThrowBadRequestException() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approve(2L, 1L, false));

        assertEquals("Booking status can't change after approval", exception.getMessage());
    }

    @Test
    void whenApproveNotOwner_thenThrowNotFoundException() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(-1L, 1L, true));

        assertEquals("Item has not been added user id -1", exception.getMessage());
    }

    @Test
    void whenApproveWithApprovedTrue_thenReturnApprovedBooking() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.approve(2L, 1L, true);

        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void whenApproveWithApprovedFalse_thenReturnRejectedBooking() {
        User booker = createUser(1L);
        User owner = createUser(2L);
        Item item = createItem(1L, owner);

        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto
                = bookingService.approve(2L, 1L, false);

        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    void whenGetAllBookingsByOwnerId_thenCallBookingRepositoryFindAllBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwner(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.ALL, 0, 20);

        verify(bookingRepository, times(1)).findAllByOwner(anyLong(), any());
    }

    @Test
    void whenGetAllEmptyBookingsByOwnerId_thenThrowNotFoundException() {
        List<Booking> bookings = List.of();

        when(bookingRepository.findAllByOwner(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getByOwnerId(4L, BookingState.ALL, 0, 20));

        assertEquals("No bookings for owner with id 4", exception.getMessage());
    }

    @Test
    void whenGetPastBookingsByOwnerId_thenCallBookingRepositoryFindPastBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwnerAndPastState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.PAST, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByOwnerAndPastState(anyLong(), any());
    }

    @Test
    void whenGetFutureBookingsByOwnerId_thenCallBookingRepositoryFindFutureBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwnerAndFutureState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.FUTURE, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByOwnerAndFutureState(anyLong(), any());
    }

    @Test
    void whenGetCurrentBookingsByOwnerId_thenCallBookingRepositoryFindCurrentBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwnerAndCurrentState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.CURRENT, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByOwnerAndCurrentState(anyLong(), any());
    }

    @Test
    void whenGetWaitingBookingsByOwnerId_thenCallBookingRepositoryFindWaitingBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwnerIdAndStatus(4L,
                BookingStatus.WAITING,
                PageRequest.of(0, 20, Sort.by("start")
                                          .descending())))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.WAITING, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByOwnerIdAndStatus(4L,
                        BookingStatus.WAITING,
                        PageRequest.of(0, 20, Sort.by("start")
                                                  .descending()));
    }

    @Test
    void whenGetRejectedBookingsByOwnerId_thenCallBookingRepositoryFindRejectedBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByOwnerIdAndStatus(4L,
                BookingStatus.REJECTED,
                PageRequest.of(0, 20, Sort.by("start")
                                          .descending())))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByOwnerId(4L, BookingState.REJECTED, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByOwnerIdAndStatus(4L,
                        BookingStatus.REJECTED,
                        PageRequest.of(0, 20, Sort.by("start")
                                                  .descending()));
    }

    @Test
    void whenGetAllBookingsByBookerId_thenCallBookingRepositoryFindAllBookings() {
        List<Booking> bookings = createOwnerBookingsList();

        when(bookingRepository.findAllByBooker(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.ALL, 0, 20);

        verify(bookingRepository, times(1)).findAllByBooker(anyLong(), any());
    }

    @Test
    void whenGetAllEmptyBookingsByBookerId_thenThrowNotFoundException() {
        List<Booking> bookings = List.of();

        when(bookingRepository.findAllByBooker(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getByBookerId(4L, BookingState.ALL, 0, 20));

        assertEquals("No bookings for booker with id 4", exception.getMessage());
    }

    @Test
    void whenGetPastBookingsByBookerId_thenCallBookingRepositoryFindPastBookings() {
        List<Booking> bookings = createBookerBookingsList();

        when(bookingRepository.findAllByBookerAndPastState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.PAST, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByBookerAndPastState(anyLong(), any());
    }

    @Test
    void whenGetFutureBookingsByBookerId_thenCallBookingRepositoryFindFutureBookings() {
        List<Booking> bookings = createBookerBookingsList();

        when(bookingRepository.findAllByBookerAndFutureState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.FUTURE, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByBookerAndFutureState(anyLong(), any());
    }

    @Test
    void whenGetCurrentBookingsByBookerId_thenCallBookingRepositoryFindCurrentBookings() {
        List<Booking> bookings = createBookerBookingsList();

        when(bookingRepository.findAllByBookerAndCurrentState(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.CURRENT, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByBookerAndCurrentState(anyLong(), any());
    }

    @Test
    void whenGetWaitingBookingsByBookerId_thenCallBookingRepositoryFindWaitingBookings() {
        List<Booking> bookings = createBookerBookingsList();

        when(bookingRepository.findAllByBookerIdAndStatus(4L,
                BookingStatus.WAITING,
                PageRequest.of(0, 20, Sort.by("start")
                                          .descending())))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.WAITING, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(4L,
                        BookingStatus.WAITING,
                        PageRequest.of(0, 20, Sort.by("start")
                                                  .descending()));
    }

    @Test
    void whenGetRejectedBookingsByBookerId_thenCallBookingRepositoryFindRejectedBookings() {
        List<Booking> bookings = createBookerBookingsList();

        when(bookingRepository.findAllByBookerIdAndStatus(4L,
                BookingStatus.REJECTED,
                PageRequest.of(0, 20, Sort.by("start")
                                          .descending())))
                .thenReturn(new PageImpl<>(bookings));

        bookingService.getByBookerId(4L, BookingState.REJECTED, 0, 20);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(4L,
                        BookingStatus.REJECTED,
                        PageRequest.of(0, 20, Sort.by("start")
                                                  .descending()));
    }

    private Booking createBooking(Long id, Item item, User booker) {
        return new Booking(id, LocalDateTime.now(), LocalDateTime.now(),
                item, booker, BookingStatus.WAITING);
    }

    private BookingRequestDto createBookingDto() {
        return BookingRequestDto.builder()
                         .itemId(1L)
                         .start(LocalDateTime.now()
                                             .plusMinutes(10))
                         .end(LocalDateTime.now()
                                           .plusMinutes(20))
                         .build();
    }

    private Item createItem(Long id, User owner) {
        return new Item(id, "Item" + id, "Description" + id, true, owner);
    }

    private User createUser(Long id) {
        return User.builder()
                   .id(id)
                   .name("user" + id)
                   .email("user" + id + "@email.ru")
                   .build();
    }

    private List<Booking> createBookerBookingsList() {
        User booker = createUser(4L);

        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        User owner3 = createUser(3L);

        Item item1 = createItem(1L, owner1);
        Item item2 = createItem(2L, owner2);
        Item item3 = createItem(3L, owner3);

        Booking booking1 = createBooking(1L, item1, booker);
        Booking booking2 = createBooking(2L, item2, booker);
        Booking booking3 = createBooking(3L, item3, booker);

        return List.of(booking1, booking2, booking3);
    }

    private List<Booking> createOwnerBookingsList() {
        User booker1 = createUser(1L);
        User booker2 = createUser(2L);
        User booker3 = createUser(3L);

        User owner = createUser(4L);

        Item item1 = createItem(1L, owner);
        Item item2 = createItem(2L, owner);
        Item item3 = createItem(3L, owner);

        Booking booking1 = createBooking(1L, item1, booker1);
        Booking booking2 = createBooking(2L, item2, booker2);
        Booking booking3 = createBooking(3L, item3, booker3);

        return List.of(booking1, booking2, booking3);
    }

}
