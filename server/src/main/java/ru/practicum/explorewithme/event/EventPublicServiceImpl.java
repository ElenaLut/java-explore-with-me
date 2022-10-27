package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.client.EndpointHit;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.IncorrectRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categoriesId, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean available, String sort, int from, int size, HttpServletRequest request) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<EventFullDto> events = eventRepository.findEventsPublic(text, categoriesId, paid, rangeStart, rangeEnd)
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
        if (available) {
            events = events.stream().filter(event -> event.getConfirmedRequests() < event.getParticipantLimit() || event.getParticipantLimit() == 0)
                    .collect(Collectors.toList());
        }
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                events = events.stream()
                        .sorted(Comparator.comparing(EventFullDto::getEventDate))
                        .collect(Collectors.toList());
            } else if (sort.equals("VIEWS")) {
                events = events.stream()
                        .sorted(Comparator.comparingLong(EventFullDto::getViews))
                        .collect(Collectors.toList());
            } else {
                log.error("Сортировки {} не существует", sort);
                throw new IncorrectRequestException("Некорректная сортировка");
            }
        }
        events.forEach(e -> e.setViews(e.getViews() + 1));
        saveStatisticHit(request);
        List<EventShortDto> shortEvents = events.stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventShortDtoFromEventFullDto)
                .collect(Collectors.toList());
        log.info("Получен список событий");
        return shortEvents;
    }

    @Override
    public EventFullDto getFullEvent(Long id, HttpServletRequest httpServletRequest) {
        Event event = getEventById(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("запрошенное событие не опубликовано");
            throw new ForbiddenException("Получить информацию можно только о опубликованных событиях");
        }
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        saveStatisticHit(httpServletRequest);
        return eventMapper.toEventFullDto(event);
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id " + id));
    }

    private void saveStatisticHit(HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-server");
        endpointHit.setUri(request.getRemoteAddr());
        endpointHit.setIp(request.getRequestURI());
        endpointHit.setTimestamp(LocalDateTime.now());
        statsClient.hit(endpointHit);
    }
}