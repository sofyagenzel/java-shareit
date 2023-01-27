package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
}