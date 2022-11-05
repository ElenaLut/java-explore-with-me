package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getEvents(String text,
                                  List<Long> categoriesId,
                                  boolean paid,
                                  LocalDateTime start,
                                  LocalDateTime end,
                                  boolean available,
                                  String sort,
                                  int from,
                                  int size,
                                  HttpServletRequest request);

    EventFullDto getFullEvent(Long id, HttpServletRequest httpServletRequest);
}
