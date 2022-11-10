package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.category.CategoryRepository;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.comment.CommentMapper;
import ru.practicum.explorewithme.comment.CommentRepository;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.dto.UpdateEventRequestDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.request.RequestMapper;
import ru.practicum.explorewithme.request.RequestRepository;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.request.dto.RequestStatus;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.user.UserRepository;
import ru.practicum.explorewithme.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<EventFullDto> getEventsByAuthor(Long userId, int from, int size) {
        getUserById(userId);
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto changeEventByAuthor(UpdateEventRequestDto updateEventRequestDto, Long userId) {
        Event event = getEventById(updateEventRequestDto.getEventId());
        Optional.ofNullable(updateEventRequestDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventRequestDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventRequestDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventRequestDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventRequestDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventRequestDto.getTitle()).ifPresent(event::setTitle);
        if (updateEventRequestDto.getCategory() != null) {
            event.setCategory(new Category(updateEventRequestDto.getCategory(), null));
        }
        event.setState(EventState.PENDING);
        log.info("Событие {} обновлено", event.getId());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        Event event = eventMapper.toEvent(newEventDto, userId);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s not found", newEventDto.getCategory())));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with if %s not found", userId)));
        event.setInitiator(user);
        event.setCategory(category);
        Event newEvent = eventRepository.save(event);
        log.info("Собитие {} создано", event.getId());
        return eventMapper.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto getFullInfoByAuthor(Long eventId, Long userId) {
        Event event = getEventById(eventId);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(event.getId(), CommentState.PUBLISHED);
        eventFullDto.setComments(comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList()));
        return eventFullDto;
    }

    @Override
    public EventFullDto cancelEventByAuthor(Long eventId, Long userId) {
        Event event = getEventById(eventId);
        event.setState(EventState.CANCELED);
        log.info("Событие {} отменено", eventId);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnAuthorEvent(Long eventId, Long userId) {
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequestByAuthor(Long eventId, Long reqId, Long userId) {
        EventFullDto eventFullDto = getFullInfoByAuthor(eventId, userId);
        if (eventFullDto.getParticipantLimit() != null
                && eventFullDto.getConfirmedRequests() >= eventFullDto.getParticipantLimit()) {
            log.error("Максимальное число участников не может быть больше {}", eventFullDto.getParticipantLimit());
            throw new ForbiddenException("Превышено максимальное количество участников");
        }
        Request request = getRequestById(reqId);
        request.setStatus(RequestStatus.CONFIRMED);
        log.info("Запрос {} подвержден", request.getId());
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequestByAuthor(Long eventId, Long reqId, Long userId) {
        EventFullDto eventFullDto = getFullInfoByAuthor(eventId, userId);
        if (eventFullDto.getParticipantLimit() != null
                && eventFullDto.getConfirmedRequests() >= eventFullDto.getParticipantLimit()) {
            log.error("Максимальное число участников не может быть больше {}", eventFullDto.getParticipantLimit());
            throw new ForbiddenException("Превышено максимальное количество участников");
        }
        Request request = getRequestById(reqId);
        request.setStatus(RequestStatus.REJECTED);
        log.info("Запрос {} отклонен", request.getId());
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
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