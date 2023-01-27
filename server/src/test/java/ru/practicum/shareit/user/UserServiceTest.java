package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto(1L, "user", "user@email.ru");
        user = new User(1L, "user", "user@email.ru");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        UserDto userDto1 = userService.createUser(userDto);
        assertEquals(userDto.getId(), userDto1.getId());
        assertEquals(userDto.getEmail(), userDto1.getEmail());
        assertEquals(userDto.getName(), userDto1.getName());
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        UserDto userDto1 = new UserDto(1L, "userUpdate", "userUpdate@email.ru");
        UserDto user1 = userService.updateUser(1L, userDto1);
        assertEquals(userDto1.getId(), user1.getId());
        assertEquals(userDto1.getEmail(), user1.getEmail());
        assertEquals(userDto1.getName(), user1.getName());
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        UserDto user1 = userService.getById(1L);
        assertEquals(user1.getId(), user.getId());
        assertEquals(user1.getEmail(), user.getEmail());
        assertEquals(user1.getName(), user.getName());
    }

    @Test
    void getUserByIdNotExistsTest() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> userService.getById(6L));
    }

    @Test
    void findAllTest() {
        List<User> users = new ArrayList<>(Collections.singletonList(user));
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersList = userService.findAll();
        assertEquals(usersList.size(), 1);
    }

    @Test
    void removeUserByIdFailIdTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.removeUser(user.getId());
        assertThrows(ObjectNotFoundException.class, () -> userService.removeUser(7L));
    }
}