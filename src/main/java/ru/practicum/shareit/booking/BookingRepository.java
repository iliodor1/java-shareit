package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.owner.id = ?2 " +
            "and b.end < current_timestamp " +
            "order by b.end desc")
    Optional<Booking> findLastBooking(Long itemId, Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 and b.item.owner.id = ?2 and b.start > current_timestamp " +
            "order by b.start")
    Optional<Booking> findNextBooking(Long itemId, Long ownerId);

    @Query("select b from Booking b where b.booker.id = ?1")
    Page<Booking> findAllByBooker(Long id, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.start > current_timestamp")
    Page<Booking> findAllByBookerAndFutureState(Long id, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.end < current_timestamp")
    Page<Booking> findAllByBookerAndPastState(Long id, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.end < current_timestamp")
    Optional<Booking> findByBookerIdAndPastState(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.start < current_timestamp and b.end > current_timestamp")
    Page<Booking> findAllByBookerAndCurrentState(Long id, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Page<Booking> findAllByOwner(Long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 and b.end < current_timestamp")
    Page<Booking> findAllByOwnerAndPastState(Long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 and b.start > current_timestamp")
    Page<Booking> findAllByOwnerAndFutureState(Long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 and b.start < current_timestamp and b.end > current_timestamp")
    Page<Booking> findAllByOwnerAndCurrentState(Long ownerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);


}
