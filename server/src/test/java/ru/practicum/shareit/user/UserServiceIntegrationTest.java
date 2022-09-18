package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
class UserServiceIntegrationTest {

    private final UserService service;
    private final EntityManager entityManager;

    @Test
    public void whenCreate_thenReturnUser() {
        UserDto userDto = createUserDto(1L);

        service.create(userDto);

        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u where u.email = : email", User.class
        );
        User user = query.setParameter("email", userDto.getEmail())
                         .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void whenUpdateEmail_thenReturnUpdatedUser() {
        UserDto userDto = service.create(createUserDto(1L));

        UserDto userWithNewEmailDto = UserDto.builder()
                                             .email("updated@mail.ru")
                                             .build();

        service.update(userDto.getId(), userWithNewEmailDto);

        TypedQuery<User> queryWithNewEmail = entityManager.createQuery(
                "select u from User u where u.id = : id", User.class
        );

        User updatedUser = queryWithNewEmail.setParameter("id", userDto.getId())
                                            .getSingleResult();

        assertThat(updatedUser.getId(), notNullValue());
        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userWithNewEmailDto.getEmail()));
    }

    @Test
    public void whenGetUser_thenReturnUser() {
        UserDto userDto = service.create(createUserDto(1L));

        service.getUser(userDto.getId());

        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u where u.id = : id", User.class
        );

        User user = query.setParameter("id", userDto.getId())
                         .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void whenDelete() {
        UserDto userDto = service.create(createUserDto(1L));

        service.delete(userDto.getId());

        User user = entityManager.find(User.class, userDto.getId());

        assertThat(user, nullValue());
    }

    @Test
    public void whenGetAll_thenReturnListOfUsersDto() {
        List<UserDto> usersDto = List.of(
                createUserDto(1L),
                createUserDto(2L),
                createUserDto(3L)
        );

        for (UserDto user : usersDto) {
            User entity = UserMapper.toUser(user);
            entityManager.persist(entity);
        }

        List<UserDto> targetUsers = service.getAll();

        assertThat(targetUsers, hasSize(usersDto.size()));
        for (UserDto userDto : usersDto) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    private UserDto createUserDto(Long id) {
        return UserDto.builder()
                      .name("user" + id)
                      .email("user" + id + "@mail.ru")
                      .build();
    }

}
