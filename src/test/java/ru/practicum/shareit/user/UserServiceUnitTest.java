package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto USER_ID_1_DTO = new UserDto(1L, "User1", "user1@mail.ru");
    private final User USER_ID_1 = new User(1L, "User1", "user1@mail.ru");
    private final User USER_ID_2 = new User(2L, "User2", "user2@mail.ru");


    @Test
    public void whenCreate_thenCallUserRepository() {
        Mockito.when(userRepository.save(any()))
               .thenReturn(USER_ID_1);

        userService.create(USER_ID_1_DTO);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void whenUpdateUser_thenCallUserRepository() {
        UserDto InputUserDto = new UserDto(null, "UpdateUser1", null);
        User UpdatedUser = new User(1L, "UpdateUser1", "user1@mail.ru");

        Mockito.when(userRepository.save(any()))
               .thenReturn(UpdatedUser);

        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.of(USER_ID_1));

        userService.update(1L, InputUserDto);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void whenUpdateUserName_thenReturnUpdatedUserWithUpdatedName() {
        UserDto InputUserDto = new UserDto(null, "UpdateUser1", null);
        User UpdatedUser = new User(1L, "UpdateUser1", "user1@mail.ru");

        Mockito.when(userRepository.save(any()))
               .thenReturn(UpdatedUser);

        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.of(USER_ID_1));

        UserDto userDto = userService.update(anyLong(), InputUserDto);

        assertEquals("UpdateUser1", userDto.getName());
    }

    @Test
    public void whenUpdateEmail_thenReturnUpdatedUserWithUpdatedEmail() {
        UserDto InputUserDto = new UserDto(null, null, "userUpdated1@mail.ru");
        User UpdatedUser = new User(1L, "User1", "userUpdated1@mail.ru");

        Mockito.when(userRepository.save(any()))
               .thenReturn(UpdatedUser);

        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.of(USER_ID_1));

        UserDto userDto = userService.update(anyLong(), InputUserDto);

        assertEquals("userUpdated1@mail.ru", userDto.getEmail());
    }

    @Test
    public void whenGetUserByIdExist_thenCallUserRepository() {
        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.of(USER_ID_1));

        userService.getUser(anyLong());

        Mockito.verify(userRepository, times(1))
               .findById(anyLong());
    }

    @Test
    public void whenGetUserByIdExist_thenReturnUserDto() {
        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.of(USER_ID_1));

        UserDto userOutputDto = userService.getUser(anyLong());

        assertEquals(USER_ID_1_DTO, userOutputDto);
    }

    @Test
    public void whenGetUserByIdNotExist_thenThrowNotFoundException() {
        Mockito.when(userRepository.findById(anyLong()))
               .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUser(-99L));

        assertEquals("User with id -99 not found!", exception.getMessage());
    }


    @Test
    public void whenDeleteById_thenCallUserRepository() {
        userService.delete(anyLong());

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void whenGetAllUsers_thenCallUserRepository() {
        Mockito.when(userRepository.findAll())
               .thenReturn(List.of(USER_ID_1, USER_ID_2));

        userService.getAll();

        Mockito.verify(userRepository, times(1))
               .findAll();
    }

    @Test
    public void whenGetAllUsers_thenReturnUsersList() {
        Mockito.when(userRepository.findAll())
               .thenReturn(List.of(USER_ID_1, USER_ID_2));

        List<UserDto> users = userService.getAll();

        assertEquals(2, users.size());
    }

}
