package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest newItemRequest = new ItemRequest();
        RequestMapper.toItemRequest(newItemRequest, itemRequestDto);
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден" + userId));
        }
        newItemRequest.setRequester(owner.get());
        newItemRequest.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(newItemRequest);
        return RequestMapper.toItemRequestDto(createdItemRequest);
    }

    @Override
    public ItemRequestDto getRequest(Long id, Long userId) {
        var itemRequest = itemRequestRepository.findById(id);
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден" + userId));
        }
        if (itemRequest.isPresent()) {
            var itemRequestDto = RequestMapper.toItemRequestDto(itemRequest.get());
            return addItemRequest(itemRequestDto);
        } else {
            throw new ObjectNotFoundException("Запрос не найден" + id);
        }
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId, Integer from, Integer size) {
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден" + userId));
        }
        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(from, size);
        }
        return itemRequestRepository.findAllByRequesterId(userId, pageable)
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .map(this::addItemRequest)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        var owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден" + userId));
        }
        Pageable pageable;
        if (from == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(from, size, Sort.by("created").descending());
        }

        return itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable)
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .map(this::addItemRequest)
                .collect(Collectors.toList());
    }

    private ItemRequestDto addItemRequest(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        RequestMapper.toItemRequest(itemRequest, dto);
        var items = itemRepository.findAllByRequestId(itemRequest.getId());
        if (items != null) {
            dto.setItems(items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }
        return dto;
    }
}