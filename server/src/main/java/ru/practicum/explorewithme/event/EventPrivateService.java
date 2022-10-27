package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.dto.UpdateEventRequestDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    List<EventFullDto> getEventsByAuthor(Long userId, int from, int size);

    EventFullDto changeEventByAuthor(UpdateEventRequestDto updateEventRequestDto, Long userId);

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getFullInfoByAuthor(Long eventId, Long userId);

    EventFullDto cancelEventByAuthor(Long eventId, Long userId);

    List<ParticipationRequestDto> getRequestsOnAuthorEvent(Long eventId, Long userId);

    ParticipationRequestDto confirmRequestByAuthor(Long eventId, Long reqId, Long userId);

    ParticipationRequestDto rejectRequestByAuthor(Long eventId, Long reqId, Long userId);

    Event getEventById(Long id);
}