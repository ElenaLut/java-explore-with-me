package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.IncorrectRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        log.info("Создана подборка {}", compilation.getId());
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventsOfCompilation(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long id) {
        Compilation compilation = getCompilationById(id);
        compilationRepository.delete(compilation);
        log.info("Подборка {} удалена", compilation.getId());
    }

    @Transactional
    @Override
    public void deleteEventInsideCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = compilation.getEvents()
                .stream()
                .filter(e -> e.getId().equals(eventId))
                .findAny()
                .orElseThrow(() ->
                        new IncorrectRequestException(
                                String.format("В подборке %s нет события %s", compId, eventId)));
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void addEventInsideCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = getEventById(eventId);
        List<Event> eventsOfCompilation = getEventsOfCompilation(compilation)
                .stream()
                .map(eventMapper::toEventFromEventShortDto)
                .collect(Collectors.toList());
        if (eventsOfCompilation.contains(event)) {
            log.error("Событие {} уже добавлено в подборку {}", event.getId(), compId);
            throw new ForbiddenException("Невозможно повторно добавить событие в подборку");
        }
        eventsOfCompilation.add(event);
        compilation.setEvents(eventsOfCompilation);
        log.info("Событие {} добавлено в подборку {}", event.getId(), compId);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void unpinCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (!compilation.isPinned()) {
            log.error("Подборка не закреплена");
            throw new ForbiddenException("Попытка открепить незакрепленную подборку");
        }
        compilationRepository.setCompilationPinned(false, compId);
    }

    @Transactional
    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (compilation.isPinned()) {
            log.error("Подборка закреплена");
            throw new ForbiddenException("Попытка закрепить закрепленную подборку");
        }
        compilationRepository.setCompilationPinned(true, compId);
    }

    private List<EventShortDto> getEventsOfCompilation(Compilation compilation) {
        List<Event> events = new ArrayList<>(compilation.getEvents());
        return events
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private Compilation getCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с id " + id));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id " + id));
    }
}
