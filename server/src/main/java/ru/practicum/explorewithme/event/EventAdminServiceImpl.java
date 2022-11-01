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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               LocalDateTime start,
                                               LocalDateTime end,
                                               int from,
                                               int size) {
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
        Optional.ofNullable(adminUpdateEventRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(adminUpdateEventRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(adminUpdateEventRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(adminUpdateEventRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(adminUpdateEventRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(adminUpdateEventRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(adminUpdateEventRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(adminUpdateEventRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(new Location(adminUpdateEventRequest.getLocation().getLat(), adminUpdateEventRequest.getLocation().getLon()));
        }
        if (adminUpdateEventRequest.getCategory() != null) {
            event.setCategory(new Category(adminUpdateEventRequest.getCategory(), null));
        }
        log.info("Событие {} обновлено", event.getId());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEvent(Long id) {
        Event event = getEventById(id);
        if (event.getState() != EventState.PENDING) {
            log.error("Событие {} не на модерации", event.getId());
            throw new ForbiddenException("Опубликовать можно только события на модерации");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) && event.getEventDate() != null) {
            log.error("Событие {} начинается менее чем через час", event.getId());
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
        if (event.getState() != EventState.PENDING) {
            log.error("Событие {} не на модерации", event.getId());
            throw new ForbiddenException("Отменить можно только события на модерации");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.error("Событие {} начинается менее чем через час", event.getId());
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
