package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository repository;


    @Test
    public void whenFindAllByRequesterIdOrderByCreated_thenReturnListOfItemRequest() {
        //given
        User requester = createUser(1L);
        entityManager.persist(requester);
        User anotherUser = createUser(2L);
        entityManager.persist(anotherUser);

        List<ItemRequest> requests = List.of(
                createItemRequest(requester),
                createItemRequest(requester),
                createItemRequest(requester),
                createItemRequest(anotherUser)
        );

        requests.forEach(entityManager::persist);

        //when
        List<ItemRequest> receivedRequests =
                repository.findAllByRequesterIdOrderByCreated(requester.getId());

        //then
        assertThat(receivedRequests, hasSize(requests.size() - 1));

        for (ItemRequest request : requests) {
            assertThat(receivedRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(request.getDescription())),
                    hasProperty("requester", equalTo(requester))
            )));
        }
    }

    private ItemRequest createItemRequest(User requester) {
        return new ItemRequest(
                null,
                "description",
                requester,
                LocalDateTime.now()
        );
    }

    private User createUser(Long id) {
        return User.builder()
                   .name("user" + id)
                   .email("user" + id + "@mail.ru")
                   .build();
    }

}
