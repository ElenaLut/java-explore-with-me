package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, int from, int size) {
        if (start == null) {
            start = LocalDateTime.now();
        }
        List<EventState> listEventState = new ArrayList<>();
        if (states.isEmpty()) {
            listEventState = null;
        } else {
            for (String state : states) {
                try {
                    listEventState.add(EventState.valueOf(state));
                } catch (IllegalArgumentException o) {
                    throw new IllegalArgumentException("Некорректный статус события: " + state);
                }
            }
        }
        return eventRepository.getEventsByAdmin(users, listEventState, categories, start, end, PageRequest.of(from / size, size))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto changeEventByAdmin(AdminUpdateEventRequest adminUpdateEventRequest, Long id) {
        Event event = getEventById(id);
        if (adminUpdateEventRequest.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }
        if (adminUpdateEventRequest.getCategory() != null) {
            event.setCategory(new Category(adminUpdateEventRequest.getCategory(), null));
        }
        if (adminUpdateEventRequest.getDescription() != null) {
            event.setDescription(adminUpdateEventRequest.getDescription());
        }
        if (adminUpdateEventRequest.getEventDate() != null) {
            event.setEventDate(adminUpdateEventRequest.getEventDate());
        }
        if (adminUpdateEventRequest.getPaid() != null) {
            event.setPaid(adminUpdateEventRequest.getPaid());
        }
        if (adminUpdateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        }
        if (adminUpdateEventRequest.getTitle() != null) {
            event.setTitle(adminUpdateEventRequest.getTitle());
        }
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(new Location(adminUpdateEventRequest.getLocation().getLat(), adminUpdateEventRequest.getLocation().getLon()));
        }
        if (adminUpdateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }
        log.info("Событие {} обновлено", event.getId());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEvent(Long id) {
        Event event = getEventById(id);
        if (!event.getState().equals(EventState.PENDING)) {
            log.error("Событие {} не на модерации", event.getId());
            throw new ForbiddenException("Опубликовать можно только события на модерации");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.error("Собитие {} начинается менее чем через час", event.getId());
            throw new ForbiddenException(" Опубликовать событие можно максимум за час до его начала");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        log.info("Событие {} опубликовано", event.getId());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long id) {
        Event event = getEventById(id);
        if (!event.getState().equals(EventState.PENDING)) {
            log.error("Событие {} не на модерации", event.getId());
            throw new ForbiddenException("Отменить можно только события на модерации");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.error("Собитие {} начинается менее чем через час", event.getId());
            throw new ForbiddenException(" Отменить событие можно максимум за час до его начала");
        }
        event.setState(EventState.CANCELED);
        log.info("Событие {} отменено", event.getId());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id " + id));
    }
}
