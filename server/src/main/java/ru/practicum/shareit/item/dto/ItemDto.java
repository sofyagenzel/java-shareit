package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@Setter
@Getter
@AllArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}