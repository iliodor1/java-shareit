package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;

    @Test
    public void whenSearchExistItem_thenReturnListOfOwnItems() {
        //given
        User owner = createUser(1L);
        entityManager.persist(owner);

        Item item1 = createItem(owner);
        item1.setDescription("Search");
        entityManager.persist(item1);

        Item item2 = createItem(owner);
        item2.setName("name_search");
        entityManager.persist(item1);

        Item item3 = createItem(owner);
        entityManager.persist(item3);

        List<Item> items = List.of(item3, item2, item1);
        items.forEach(entityManager::persist);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        List<Item> receivedItems = repository.searchItem("search", pageable);

        //then
        assertThat(receivedItems, hasSize(items.size() - 1));
        assertTrue(receivedItems.get(0)
                                .getDescription()
                                .toLowerCase()
                                .contains("search"));
        assertTrue(receivedItems.get(1)
                                .getName()
                                .toLowerCase()
                                .contains("search"));
    }

    @Test
    public void whenFindAllItemsByRequestId_thenReturnListOfItems() {
        //given
        User owner = createUser(1L);
        entityManager.persist(owner);

        User requester = createUser(2L);
        entityManager.persist(requester);

        ItemRequest itemRequest = createItemRequest(requester);
        entityManager.persist(itemRequest);

        Item item1 = createItem(owner);
        item1.setRequest(itemRequest);
        entityManager.persist(item1);

        Item item2 = createItem(owner);
        item2.setRequest(itemRequest);
        entityManager.persist(item1);

        Item item3 = createItem(owner);
        entityManager.persist(item3);

        List<Item> items = List.of(item3, item2, item1);
        items.forEach(entityManager::persist);

        //when
        List<Item> receivedItems = repository.findAllItemsByRequestId(itemRequest.getId());

        //then
        assertThat(receivedItems, hasSize(items.size() - 1));
        assertThat(receivedItems.get(0)
                                .getRequest()
                                .getRequester(), equalTo(requester));
        assertThat(receivedItems.get(1)
                                .getRequest()
                                .getRequester(), equalTo(requester));

    }

    private User createUser(Long id) {
        return User.builder()
                   .name("user" + id)
                   .email("user" + id + "@mail.ru")
                   .build();
    }

    private Item createItem(User owner) {
        return Item.builder()
                   .name("item")
                   .description("description")
                   .owner(owner)
                   .available(true)
                   .build();
    }

    private ItemRequest createItemRequest(User requester) {
        return new ItemRequest(null, "description", requester, LocalDateTime.now());
    }

}
