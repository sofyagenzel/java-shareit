package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static void toUser(User user, UserDto userDto) {
        user.setId(userDto.getId());
        Optional.ofNullable(userDto.getName()).ifPresent(x -> user.setName(userDto.getName()));
        Optional.ofNullable(userDto.getEmail()).ifPresent(x -> user.setEmail(userDto.getEmail()));
    }
}