package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    @Query("SELECT i\n"
            + "FROM Item i\n"
            + "WHERE i.available IS TRUE\n"
            + "  AND (upper(i.name) like upper(concat('%', ?1, '%'))\n"
            + "       OR upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> searchItem(String text, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    @Query("SELECT i\n"
            + "FROM Item i\n"
            + "WHERE i.request.id = ?1")
    List<Item> findAllItemsByRequestId(Long requestId);

}
