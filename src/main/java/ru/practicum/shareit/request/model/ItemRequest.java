package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private int id;
    private String description;
    private User requester;
    private LocalDate created;
}