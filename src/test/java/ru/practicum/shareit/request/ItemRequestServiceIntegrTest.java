package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringJUnitConfig({ShareItApp.class, ItemRequestServiceImpl.class})
public class ItemRequestServiceIntegrTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequestTest() {
        User user = new User(1L, "user", "user@email.ru");
        UserDto userDto = new UserDto(1L, "user", "user@email.ru");
        userService.createUser(userDto);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", user, LocalDateTime.now(), null);
        itemRequestService.createRequest(itemRequestDto, userDto.getId());
        ItemDto itemDto = new ItemDto(null, null, null, 1L, "item", "item test", true, 1L);
        itemService.createItem(itemDto, 1L);
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = : id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDto.getId()).getSingleResult();
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(itemRequestDto.getRequester().getId()));
    }
}