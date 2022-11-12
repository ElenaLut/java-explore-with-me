package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.dto.UpdateEventRequestDto;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getEventsByAuthor(@PathVariable Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        return eventPrivateService.getEventsByAuthor(userId, from, size);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto changeEventByAuthor(@Valid @RequestBody UpdateEventRequestDto updateEventRequestDto,
                                            @PathVariable Long userId) {
        return eventPrivateService.changeEventByAuthor(updateEventRequestDto, userId);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto, @PathVariable Long userId) {
        return eventPrivateService.createEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getFullInfoByAuthor(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventPrivateService.getFullInfoByAuthor(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEventByAuthor(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventPrivateService.cancelEventByAuthor(eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOnAuthorEvent(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return eventPrivateService.getRequestsOnAuthorEvent(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequestByAuthor(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @PathVariable Long reqId) {
        return eventPrivateService.confirmRequestByAuthor(eventId, reqId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequestByAuthor(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @PathVariable Long reqId) {
        return eventPrivateService.rejectRequestByAuthor(eventId, reqId, userId);
    }
}