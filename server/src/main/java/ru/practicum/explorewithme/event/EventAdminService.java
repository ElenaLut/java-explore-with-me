package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime start, LocalDateTime end, int from, int size);

    EventFullDto changeEventByAdmin(AdminUpdateEventRequest adminUpdateEventRequest, Long id);

    EventFullDto publishEvent(Long id);

    EventFullDto rejectEvent(Long id);
}
