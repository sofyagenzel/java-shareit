package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ItemResponseDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;

    @Data
    public static class BookingDto {
        private final Long id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Long bookerId;
    }
}