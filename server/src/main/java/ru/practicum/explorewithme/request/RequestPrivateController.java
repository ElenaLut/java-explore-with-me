package ru.practicum.explorewithme.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class RequestPrivateController {

    private final RequestPrivateService requestPrivateService;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId) {
        return requestPrivateService.getRequestsByUser(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequestByUser(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestPrivateService.createRequestByUser(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUser(@PathVariable Long requestId, @PathVariable Long userId) {
        return requestPrivateService.cancelRequestByUser(requestId, userId);
    }
}
