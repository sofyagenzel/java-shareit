package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = new User();
        UserMapper.toUser(user, userDto);
        User createdUser = repository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        UserMapper.toUser(user, userDto);
        user.setId(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void removeUser(Long id) {
        getById(id);
        repository.deleteById(id);
    }
}