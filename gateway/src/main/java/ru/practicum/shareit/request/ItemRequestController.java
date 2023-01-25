package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        return requestClient.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getRequest(requestId, userId);
    }
}