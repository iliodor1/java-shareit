package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
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

        ItemOutputDto createdItem = service.getItem(item.getId(), owner.getId());

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

        List<ItemOutputDto> ownItems = service.getOwnItems(owner.getId(), 0, 20);

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

        ItemInputDto itemInputDto = createItemInputDto(1L);

        ItemInputDto createdItem = service.create(owner.getId(), itemInputDto);

        assertThat(createdItem.getId(), notNullValue());
        assertThat(itemInputDto.getName(), equalTo(createdItem.getName()));
        assertThat(itemInputDto.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemInputDto.getAvailable(), equalTo(createdItem.getAvailable()));
    }

    @Test
    public void whenUpdateDescription_thenReturnUpdatedItemInputDto() {
        User owner = createUser(1L);
        Item item = createItem(1L, owner);
        entityManager.persist(owner);
        entityManager.persist(item);
        ItemInputDto itemInputDto = ItemInputDto.builder()
                                                .available(false)
                                                .description("updatedDescription")
                                                .build();

        ItemInputDto updatedItem
                = service.update(owner.getId(), item.getId(), itemInputDto);

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(item.getId(), equalTo(updatedItem.getId()));
        assertThat(item.getName(), equalTo(updatedItem.getName()));
        assertThat(itemInputDto.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(itemInputDto.getAvailable(), equalTo(updatedItem.getAvailable()));
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

        List<ItemInputDto> foundItems = service.searchItem("search", 0, 20);

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
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
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

    private ItemInputDto createItemInputDto(Long id) {
        return ItemInputDto.builder()
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
