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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    Pageable pageable;

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
                .map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return itemRequestResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId, Integer from, Integer size) {
        List<ItemRequestResponseDto> itemRequestList = new ArrayList<>();
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        Pageable pageable;
        pageable = PageRequest.of(from, size);
        List<ItemRequest> itemRequestDb = itemRequestRepository.findAllByRequesterId(userId, pageable).getContent();
        for (ItemRequest itemRequest : itemRequestDb) {
            ItemRequestResponseDto itemRequestResponseDto = RequestMapper.toItemRequestResponseDto(itemRequest);
            itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemDto).collect(Collectors.toList()));
            itemRequestList.add(itemRequestResponseDto);
        }
        return itemRequestList;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        Long itemRequestId;
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь не найден" + userId)));
        pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequestDb = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable).stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .collect(Collectors.toList());
        List<ItemDto> itemList = itemRepository.findAll().stream()
                .filter(item -> item.getRequest() != null).map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<ItemRequestResponseDto> itemRequestList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestDb) {
            itemRequestId = itemRequest.getId();
            ItemRequestResponseDto itemRequestResponseDto = RequestMapper.toItemRequestResponseDto(itemRequest);
            for (ItemDto itemL : itemList) {
                if (itemL.getRequestId().equals(itemRequestId)) {
                    itemRequestResponseDto.setItems(itemList);
                    itemRequestList.add(itemRequestResponseDto);
                }
            }
        }
        return itemRequestList;
    }
}