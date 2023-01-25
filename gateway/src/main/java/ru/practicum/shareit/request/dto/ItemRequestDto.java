package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
}