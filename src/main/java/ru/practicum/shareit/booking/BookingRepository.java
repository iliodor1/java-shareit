package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.id = ?1\n"
      + "  AND b.item.owner.id = ?2\n"
      + "  AND b.end < CURRENT_TIMESTAMP\n"
      + "ORDER BY b.end DESC")
  Optional<Booking> findLastBooking(Long itemId, Long ownerId);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.id = ?1\n"
      + "  AND b.item.owner.id = ?2\n"
      + "  AND b.start > CURRENT_TIMESTAMP\n"
      + "ORDER BY b.start")
  Optional<Booking> findNextBooking(Long itemId, Long ownerId);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.booker.id = ?1")
  Page<Booking> findAllByBooker(Long id, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.booker.id = ?1\n"
      + "  AND b.start > CURRENT_TIMESTAMP")
  Page<Booking> findAllByBookerAndFutureState(Long id, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.booker.id = ?1\n"
      + "  AND b.end < CURRENT_TIMESTAMP")
  Page<Booking> findAllByBookerAndPastState(Long id, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.booker.id = ?1\n"
      + "  AND b.end < CURRENT_TIMESTAMP")
  Optional<Booking> findByBookerIdAndPastState(Long bookerId);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.booker.id = ?1\n"
      + "  AND b.start < CURRENT_TIMESTAMP\n"
      + "  AND b.end > CURRENT_TIMESTAMP")
  Page<Booking> findAllByBookerAndCurrentState(Long id, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.owner.id = ?1")
  Page<Booking> findAllByOwner(Long ownerId, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.owner.id = ?1\n"
      + "  AND b.end < CURRENT_TIMESTAMP")
  Page<Booking> findAllByOwnerAndPastState(Long ownerId, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.owner.id = ?1\n"
      + "  AND b.start > CURRENT_TIMESTAMP")
  Page<Booking> findAllByOwnerAndFutureState(Long ownerId, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.owner.id = ?1\n"
      + "  AND b.start < CURRENT_TIMESTAMP\n"
      + "  AND b.end > CURRENT_TIMESTAMP")
  Page<Booking> findAllByOwnerAndCurrentState(Long ownerId, Pageable pageable);

  Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

  @Query("SELECT b\n"
      + "FROM Booking b\n"
      + "WHERE b.item.owner.id = ?1\n"
      + "  AND b.status = ?2")
  Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

}
