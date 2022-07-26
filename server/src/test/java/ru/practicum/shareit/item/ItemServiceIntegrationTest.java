package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
class ItemServiceIntegrationTest {

    private final ItemService service;
    private final EntityManager entityManager;

    @Test
    public void whenGetItemExist_thenReturnItemOutputDto() {
        User owner = createUser(1L);
        Item item = createItem(1L, owner);
        entityManager.persist(owner);
        entityManager.persist(item);

        ItemResponseDto createdItem = service.getItem(item.getId(), owner.getId());

        assertThat(createdItem.getId(), notNullValue());
        assertThat(item.getName(), equalTo(createdItem.getName()));
        assertThat(item.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(item.getAvailable(), equalTo(createdItem.getAvailable()));
    }

    @Test
    public void whenGetOwnItems_thenReturnListOfItems() {
        User owner = createUser(1L);
        entityManager.persist(owner);

        List<Item> items = List.of(
                createItem(1L, owner),
                createItem(2L, owner),
                createItem(3L, owner)
        );

        items.forEach(entityManager::persist);

        List<ItemResponseDto> ownItems = service.getOwnItems(owner.getId(), 0, 20);

        assertThat(items, hasSize(ownItems.size()));

        for (Item item : items) {
            assertThat(ownItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            )));
        }
    }

    @Test
    public void whenCreate_thenReturnItemInputDto() {
        User owner = createUser(1L);
        entityManager.persist(owner);

        ItemDto itemDto = createItemInputDto(1L);

        ItemDto createdItem = service.create(owner.getId(), itemDto);

        assertThat(createdItem.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(createdItem.getName()));
        assertThat(itemDto.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(createdItem.getAvailable()));
    }

    @Test
    public void whenUpdateDescription_thenReturnUpdatedItemInputDto() {
        User owner = createUser(1L);
        Item item = createItem(1L, owner);
        entityManager.persist(owner);
        entityManager.persist(item);
        ItemDto itemDto = ItemDto.builder()
                                 .available(false)
                                 .description("updatedDescription")
                                 .build();

        ItemDto updatedItem
                = service.update(owner.getId(), item.getId(), itemDto);

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(item.getId(), equalTo(updatedItem.getId()));
        assertThat(item.getName(), equalTo(updatedItem.getName()));
        assertThat(itemDto.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(updatedItem.getAvailable()));
    }

    @Test
    public void whenSearchItem_thenReturnListOfItemsInputDto() {
        User owner = createUser(1L);
        entityManager.persist(owner);

        Item item1 = createItem(1L, owner);
        Item item2 = createItem(2L, owner);
        Item item3 = createItem(3L, owner);

        item2.setName("forSearch");
        item3.setDescription("forSearch");

        List<Item> items = List.of(item1, item2, item3);
        items.forEach(entityManager::persist);

        List<ItemDto> foundItems = service.searchItem("search", 0, 20);

        assertThat(foundItems, hasSize(items.size() - 1));
    }

    @Test
    public void whenCreateComment_thenReturnComment() {
        User booker = createUser(1L);
        entityManager.persist(booker);
        User owner = createUser(2L);
        entityManager.persist(owner);
        Item item = createItem(1L, owner);
        entityManager.persist(item);

        Booking booking = new Booking(
                null,
                LocalDateTime.now()
                             .minusHours(2),
                LocalDateTime.now()
                             .minusHours(1),
                item,
                booker,
                BookingStatus.APPROVED
        );

        entityManager.persist(booking);

        CommentDto comment = CommentDto.builder()
                                       .text("comment")
                                       .authorName("user1")
                                       .build();

        CommentDto createdComment = service.createComment(booker.getId(), item.getId(), comment);

        assertThat(createdComment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(createdComment.getText()));
        assertThat(comment.getAuthorName(), equalTo(createdComment.getAuthorName()));
    }

    private Item createItem(Long id, User owner) {
        return new Item(null, "item" + id, "description" + id, true, owner);
    }

    private ItemDto createItemInputDto(Long id) {
        return ItemDto.builder()
                      .name("item" + id)
                      .description("description" + id)
                      .available(true)
                      .build();
    }

    private User createUser(Long id) {
        return User.builder()
                   .name("user" + id)
                   .email("user" + id + "@mail.ru")
                   .build();
    }

}
