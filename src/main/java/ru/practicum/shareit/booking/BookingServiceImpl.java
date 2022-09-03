package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithStatusDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingWithStatusDto getById(Long userId, Long bookingId) {
        Booking booking = findBookingById(bookingId);

        Long bookerId = booking.getBooker()
                               .getId();
        Long ownerId = booking.getItem()
                              .getOwner()
                              .getId();

        if (bookerId.equals(userId) || ownerId.equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            log.error("Only the item owner or the booker can getById the booking");
            throw new NotFoundException("Only the item owner or the booker can getById the booking");
        }
    }

    @Override
    public BookingWithStatusDto create(Long bookerId, BookingDto bookingDto) {
        User booker = userRepository.findById(bookerId)
                                    .orElseThrow(() -> {
                                        log.error("User with id " + bookerId + " not found!");
                                        return new NotFoundException(
                                                "User with id " + bookerId + " not found!"
                                        );
                                    });

        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> {
                                      log.error("Item with id {} not found", itemId);
                                      return new NotFoundException(
                                              "Item not found with id: " + itemId
                                      );
                                  });

        if (bookerId.equals(item.getOwner()
                                .getId())) {
            log.error("User can't booking own item");
            throw new NotFoundException("User can't booking own item");
        }

        if (item.getAvailable()
                .equals(false)) {
            throw new BadRequestException("The item is not available!");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingWithStatusDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = findBookingById(bookingId);
        if (booking.getStatus()
                   .equals(BookingStatus.APPROVED)) {
            log.error("Booking status can't change after approve");
            throw new BadRequestException("Booking status can't change after approval");
        }

        Long itemOwnerId = booking.getItem()
                                  .getOwner()
                                  .getId();

        if (!itemOwnerId.equals(ownerId)) {
            log.error("User with id {} tries to approve not own item", ownerId);
            throw new NotFoundException("Item has not been added user id " + ownerId);
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingWithStatusDto> getByBookerId(Long bookerId,
                                                    BookingState bookingState,
                                                    Integer from,
                                                    Integer size) {
        List<Booking> bookings = List.of();

        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                                                           .descending());

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(bookerId, pageable)
                                            .toList();
                if (bookings.isEmpty()) {
                    log.error("No bookings for booker with id {}", bookerId);
                    throw new NotFoundException("No bookings for booker with id " + bookerId);
                }
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndPastState(bookerId, pageable)
                                            .toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndFutureState(bookerId, pageable)
                                            .toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndCurrentState(bookerId, pageable)
                                            .toList();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId,
                                                    BookingStatus.WAITING,
                                                    pageable)
                                            .toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId,
                                                    BookingStatus.REJECTED,
                                                    pageable)
                                            .toList();
                break;
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingWithStatusDto> getByOwnerId(Long ownerId,
                                                   BookingState bookingState,
                                                   Integer from,
                                                   Integer size) {
        List<Booking> bookings = List.of();

        int page = from < size ? 0 : from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start")
                                                                  .descending());

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(ownerId, pageable)
                                            .toList();
                if (bookings.isEmpty()) {
                    log.error("No bookings for owner with id {}", ownerId);
                    throw new NotFoundException("No bookings for owner with id " + ownerId);
                }
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerAndPastState(ownerId, pageable)
                                            .toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerAndFutureState(ownerId, pageable)
                                            .toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerAndCurrentState(ownerId, pageable)
                                            .toList();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable)
                                            .toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable)
                                            .toList();
                break;
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> {
                                    log.error(
                                            "Booking with id {} not found", bookingId
                                    );
                                    return new NotFoundException(
                                            "Booking not found with id: " + bookingId
                                    );
                                });
    }

}
