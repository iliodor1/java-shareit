package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

  List<ItemRequest> findAllByRequesterIdOrderByCreated(Long requesterId);

  @Query("SELECT i\n"
      + "FROM ItemRequest i\n"
      + "WHERE i.requester.id <> ?1")
  Page<ItemRequest> findAllWithoutUserRequests(Long userId, Pageable pageable);

}
