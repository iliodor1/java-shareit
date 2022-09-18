package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService service;
    private final EntityManager entityManager;

    @Test
    public void whenGetByIdExist_thenReturnBookingWithStatusDto() {
        User booker = createUser(1L);
        User owner = createUser(2L);

        entityManager.persist(booker);
        entityManager.persist(owner);

        Item item = createItem(1L, owner);
        entityManager.persist(item);

        Booking booking = createBooking(item, booker);
        entityManager.persist(booking);

        BookingDto receivedBooking = service.getById(owner.getId(), booking.getId());

        assertThat(receivedBooking.getId(), notNullValue());
        assertThat(booking.getId(), equalTo(receivedBooking.getId()));
        assertThat(booking.getItem()
                          .getId(), equalTo(receivedBooking.getItem()
                                                           .getId()));
        assertThat(booking.getStart(), equalTo(receivedBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(receivedBooking.getEnd()));
        assertThat(booker.getId(), equalTo(receivedBooking.getBooker()
                                                          .getId()));
    }

    @Test
    public void whenCreateBooking_thenReturnBookingWithStatusDto() {
        User booker = createUser(1L);
        User owner = createUser(2L);

        entityManager.persist(booker);
        entityManager.persist(owner);

        Item item = createItem(1L, owner);
        entityManager.persist(item);

        BookingRequestDto bookingRequestDto = createBookingDto(item.getId());

        BookingDto createdBooking = service.create(booker.getId(), bookingRequestDto);

        assertThat(createdBooking.getId(), notNullValue());
        assertThat(bookingRequestDto.getItemId(), equalTo(createdBooking.getItem()
                                                                        .getId()));
        assertThat(bookingRequestDto.getStart(), equalTo(createdBooking.getStart()));
        assertThat(bookingRequestDto.getEnd(), equalTo(createdBooking.getEnd()));
        assertThat(booker.getId(), equalTo(createdBooking.getBooker()
                                                         .getId()));
    }

    @Test
    public void whenApproveBooking_thenBookingWithApprovedStatus() {
        User booker = createUser(1L);
        User owner = createUser(2L);

        entityManager.persist(booker);
        entityManager.persist(owner);
        Item item = createItem(1L, owner);
        entityManager.persist(item);

        Booking booking = createBooking(item, booker);
        entityManager.persist(booking);

        BookingDto approvedBooking = service.approve(owner.getId(), booking.getId(), true);

        assertThat(approvedBooking.getId(), notNullValue());
        assertThat(booking.getId(), equalTo(approvedBooking.getId()));
        assertThat(booking.getItem()
                          .getId(), equalTo(approvedBooking.getItem()
                                                           .getId()));
        assertThat(BookingStatus.APPROVED, equalTo(approvedBooking.getStatus()));
    }

    @Test
    public void whenGetByBookerId_thenReturnBookingsByBooker() {
        List<User> bookers = List.of(createUser(1L), createUser(2L));
        bookers.forEach(entityManager::persist);

        User owner = createUser(3L);
        entityManager.persist(owner);

        List<Item> items = List.of(
                createItem(1L, owner),
                createItem(2L, owner),
                createItem(3L, owner)
        );
        items.forEach(entityManager::persist);

        List<Booking> bookings = List.of(
                createBooking(items.get(0), bookers.get(0)),
                createBooking(items.get(1), bookers.get(0)),
                createBooking(items.get(2), bookers.get(1))
        );
        bookings.forEach(entityManager::persist);

        List<BookingDto> bookerItems
                = service.getByBookerId(bookers.get(0)
                                               .getId(), BookingState.ALL, 0, 20);

        assertThat(bookerItems, hasSize(bookings.size() - 1));

        assertThat(bookerItems.get(0)
                              .getBooker()
                              .getId(), equalTo(bookers.get(0)
                                                       .getId()));
        assertThat(bookerItems.get(1)
                              .getBooker()
                              .getId(), equalTo(bookers.get(0)
                                                       .getId()));
    }

    @Test
    public void whenGetByOwnerId_thenReturnBookingsByOwner() {
        User booker = createUser(1L);
        entityManager.persist(booker);

        List<User> owners = List.of(createUser(2L), createUser(3L));
        owners.forEach(entityManager::persist);

        List<Item> items = List.of(
                createItem(1L, owners.get(0)),
                createItem(2L, owners.get(1)),
                createItem(3L, owners.get(0))
        );
        items.forEach(entityManager::persist);

        List<Booking> bookings = List.of(
                createBooking(items.get(0), booker),
                createBooking(items.get(1), booker),
                createBooking(items.get(2), booker)
        );
        bookings.forEach(entityManager::persist);

        List<BookingDto> ownItems
                = service.getByOwnerId(owners.get(0)
                                             .getId(), BookingState.ALL, 0, 20);

        assertThat(ownItems, hasSize(bookings.size() - 1));
    }

    private Booking createBooking(Item item, User booker) {
        return new Booking(
                null,
                LocalDateTime.now()
                             .minusHours(2),
                LocalDateTime.now()
                             .minusHours(1),
                item,
                booker,
                BookingStatus.WAITING
        );
    }

    private BookingRequestDto createBookingDto(Long itemId) {
        return BookingRequestDto.builder()
                                .itemId(itemId)
                                .start(LocalDateTime.now()
                                                    .plusMinutes(10))
                                .end(LocalDateTime.now()
                                                  .plusMinutes(20))
                                .build();
    }

    private Item createItem(Long id, User owner) {
        return new Item(
                null,
                "item" + id,
                "description" + id,
                true,
                owner
        );
    }

    private User createUser(Long id) {
        return User.builder()
                   .name("user" + id)
                   .email("user" + id + "@mail.ru")
                   .build();
    }

}
