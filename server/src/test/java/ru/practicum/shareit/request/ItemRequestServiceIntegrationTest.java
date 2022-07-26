package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService service;
    private final EntityManager entityManager;

    @Test
    public void whenGetById_thenReturnItemRequestDto() {
        User user = createUser(1L);
        entityManager.persist(user);
        entityManager.flush();

        ItemRequestDto requestDto = service.create(user.getId(), createItemRequestDto(1L));

        ItemRequestDto receivedRequestDto = service.getById(user.getId(), requestDto.getId());

        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "select r from ItemRequest r where r.id = : id", ItemRequest.class
        );
        ItemRequest request = query.setParameter("id", receivedRequestDto.getId())
                                   .getSingleResult();

        assertThat(receivedRequestDto.getId(), notNullValue());
        assertThat(request.getId(), equalTo(receivedRequestDto.getId()));
        assertThat(request.getDescription(), equalTo(receivedRequestDto.getDescription()));
    }

    @Test
    public void whenCreate_thenReturnItemRequestDto() {
        User user = createUser(1L);
        entityManager.persist(user);
        entityManager.flush();

        ItemRequestDto requestDto = service.create(user.getId(), createItemRequestDto(1L));

        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "select r from ItemRequest r where r.id = : id", ItemRequest.class
        );
        ItemRequest request = query.setParameter("id", requestDto.getId())
                                   .getSingleResult();

        assertThat(requestDto.getId(), notNullValue());
        assertThat(request.getId(), equalTo(requestDto.getId()));
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    public void whenGetOwnRequests_thenReturnListOfOwnItemRequestsDto() {
        User user = createUser(1L);
        entityManager.persist(user);
        entityManager.flush();

        List<ItemRequestDto> requestsInputDto = List.of(
                createItemRequestDto(1L),
                createItemRequestDto(2L),
                createItemRequestDto(3L)
        );

        requestsInputDto.forEach(r -> service.create(user.getId(), r));

        List<ItemRequestDto> requestsDto = service.getOwnRequests(user.getId());

        assertThat(requestsDto, hasSize(requestsInputDto.size()));
        for (ItemRequestDto requestDto : requestsInputDto) {
            assertThat(requestsDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(requestDto.getDescription())),
                    hasProperty("created", equalTo(requestDto.getCreated()))
            )));
        }
    }

    @Test
    public void whenGetAllRequests_thenReturnAllRequests() {
        List<User> users = List.of(createUser(1L), createUser(2L), createUser(3L));

        users.forEach(entityManager::persist);
        entityManager.flush();

        List<ItemRequestDto> requestsInputDto = List.of(createItemRequestDto(1L),
                createItemRequestDto(2L),
                createItemRequestDto(3L));

        int i = 0;

        for (ItemRequestDto requestsDto : requestsInputDto) {
            service.create(users.get(i).getId(), requestsDto);
            i++;
        }

        List<ItemRequestDto> requestsDto =
                service.getAllRequests(users.get(2).getId(), 0, 20);

        assertThat(requestsDto, hasSize(requestsInputDto.size() - 1));
    }

    private ItemRequestDto createItemRequestDto(Long id) {
        return new ItemRequestDto(
                null,
                "description" + id,
                LocalDateTime.now(),
                null
        );
    }

    private User createUser(Long id) {
        return User.builder()
                   .name("user" + id)
                   .email("user" + id + "@mail.ru")
                   .build();
    }

}
