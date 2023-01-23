package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest newItemRequest = RequestMapper.toItemRequest(itemRequestDto);
        newItemRequest.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId))));
        newItemRequest.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(newItemRequest);
        return RequestMapper.toItemRequestResponseDto(createdItemRequest);
    }

    @Override
    public ItemRequestResponseDto getRequest(Long id, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден" + id));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        ItemRequestResponseDto itemRequestResponseDto = RequestMapper.toItemRequestResponseDto(itemRequest);
        itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto).collect(toList()));
        return itemRequestResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        Pageable pageable;
        pageable = PageRequest.of(from, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, pageable).stream()
                .collect(toList());
        return getItemRequestResponseDtos(itemRequests);
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable;
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable).stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .collect(toList());
        return getItemRequestResponseDtos(itemRequests);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<ItemRequest> itemRequests) {
        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIn(itemRequests)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(groupingBy(ItemDto::getRequestId, toList()));
        List<ItemRequestResponseDto> itemRequestList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestResponseDto itemRequestResponseDto = RequestMapper.toItemRequestResponseDto(itemRequest);
            itemRequestResponseDto.setItems(itemsByRequest.getOrDefault(itemRequest.getId(), Collections.emptyList()));
            itemRequestList.add(itemRequestResponseDto);
        }
        return itemRequestList;
    }
}