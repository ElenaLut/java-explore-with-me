package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.request.RequestRepository;
import ru.practicum.explorewithme.user.dto.UserShortDto;
import ru.practicum.explorewithme.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final RequestRepository requestRepository;

    public Event toEvent(NewEventDto eventCreateDto, Long userId) {
        return Event.builder()
                .id(null)
                .title(eventCreateDto.getTitle())
                .annotation(eventCreateDto.getAnnotation())
                .description(eventCreateDto.getDescription())
                .category(new Category(eventCreateDto.getCategory(), null))
                .createdOn(LocalDateTime.now())
                .eventDate(eventCreateDto.getEventDate())
                .initiator(new User(userId, null, null))
                .paid(eventCreateDto.getPaid())
                .participantLimit(eventCreateDto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(eventCreateDto.getRequestModeration())
                .state(EventState.PENDING)
                .location(new Location(eventCreateDto.getLocation().getLat(), eventCreateDto.getLocation().getLon()))
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .confirmedRequests(requestRepository.getConfirmedRequests(event.getId()))
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views((event.getViews()))
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(requestRepository.getConfirmedRequests(event.getId()))
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views((event.getViews()))
                .build();
    }

    public Event toEventFromEventShortDto(EventShortDto eventShortDto) {
        return Event.builder()
                .id(eventShortDto.getId())
                .title(eventShortDto.getTitle())
                .annotation(eventShortDto.getAnnotation())
                .eventDate(eventShortDto.getEventDate())
                .paid(eventShortDto.getPaid())
                .build();
    }

    public EventShortDto toEventShortDtoFromEventFullDto(EventFullDto eventFullDto) {
        return EventShortDto.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory())
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .paid(eventFullDto.getPaid())
                .title(eventFullDto.getTitle())
                .views(eventFullDto.getViews())
                .build();
    }
}
