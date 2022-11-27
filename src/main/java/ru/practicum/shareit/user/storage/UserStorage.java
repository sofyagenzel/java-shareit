package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id) throws ObjectNotFoundException;

    void removeUserById(int id);

    Boolean checkEmail(User user);
}