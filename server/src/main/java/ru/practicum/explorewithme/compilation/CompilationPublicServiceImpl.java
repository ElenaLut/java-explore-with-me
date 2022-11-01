package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public List<CompilationDto> getAllCompilations(boolean pin, int from, int size) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pin, PageRequest.of(from / size, size));
        log.info("Возвращен список подборок");
        return compilations
                .stream()
                .map(comp -> compilationMapper.toCompilationDto(comp, getEventsOfCompilation(comp)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getFullCompilation(Long id) {
        Compilation compilation = getCompilationById(id);
        log.info("Получена информации о подборке с id {}", id);
        return compilationMapper.toCompilationDto(compilation, getEventsOfCompilation(compilation));
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
}