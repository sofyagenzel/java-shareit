package ru.practicum.shareit.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectExists;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = new User();
        UserMapper.toUser(user, userDto);
        if (!userStorage.checkEmail(user)) {
            User createdUser = userStorage.createUser(user);
            return UserMapper.toUserDto(createdUser);
        } else {
            throw new ObjectExists("Пользователь с таким адресом зарегистрирован.");
        }
    }


    public UserDto updateUser(int id, UserDto userDto) {
        User updatedUser;
        User user = new User(userStorage.getUserById(id));
        UserMapper.toUser(user, userDto);
        user.setId(id);
        if (!userStorage.checkEmail(user)) {
            updatedUser = userStorage.updateUser(user);
            return UserMapper.toUserDto(updatedUser);
        } else {
            throw new ObjectExists("Пользователь с таким адресом зарегистрирован.");
        }
    }

    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    public void removeUserById(int id) {
        userStorage.getUserById(id);
        userStorage.removeUserById(id);
    }


}