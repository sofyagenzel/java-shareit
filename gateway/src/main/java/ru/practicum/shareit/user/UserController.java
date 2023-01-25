package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Validated({Update.class}) @RequestBody UserDto userDto) {
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable Long userId) {
        return userClient.removeUser(userId);
    }
}