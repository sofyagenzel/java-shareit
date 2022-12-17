package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserDto createUser(UserDto userDto) {
        User user = new User();
        UserMapper.toUser(user, userDto);
        User createdUser = repository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        var user = repository.findById(id);
        if (user.isPresent()) {
            UserMapper.toUser(user.get(), userDto);
            user.get().setId(id);
            User updatedUser = repository.save(user.get());
            return UserMapper.toUserDto(updatedUser);
        } else {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        var user = repository.findById(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(user.get());
    }

    public void removeUserById(Long id) {
        getUserById(id);
        repository.deleteById(id);
    }
}