package ru.practicum.explorewithme.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.dto.RequestStatus;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.user.UserRepository;
import ru.practicum.explorewithme.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        log.info("Получены заявки пользователя {}", userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequestByUser(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь {} является автором события {}", userId, eventId);
            throw new ForbiddenException("Пользователь ен может отправить запрос на участие на свое событие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            log.error("Событие {} не опубликовано", eventId);
            throw new ForbiddenException("Заявку на участие можно отправить только на опубликованные события");
        }
        if (event.getParticipantLimit() != 0
                && requestRepository.getConfirmedRequests(eventId) >= event.getParticipantLimit()) {
            log.error("Количество участников превышает лимит события {}", eventId);
            throw new ForbiddenException("Количество участников не может превышать лимит");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .eventId(eventId)
                .requesterId(userId)
                .build();
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        log.info("Запрос {} создан", request.getId());
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequestByUser(Long requestId, Long userId) {
        getUserById(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequesterId().equals(userId)) {
            log.error("Пользователь {} не может отменить запрос {}", userId, requestId);
            throw new ForbiddenException("Только автор запроса может отменить его");
        }
        if (request.getStatus() == RequestStatus.CANCELED) {
            log.error("Запрос {} уже отменен", requestId);
            throw new ForbiddenException("Нельзя отменить запрос, который уже был отменен");
        }
        request.setStatus(RequestStatus.CANCELED);
        log.info("Запрос {} отменен", requestId);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id " + id));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + id));
    }

    private Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id " + id));
    }
}
