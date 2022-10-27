package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.event.EventPrivateService;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventPrivateService eventPrivateService;

    public Compilation toCompilation(NewCompilationDto compilationCreateDto) {
        List<Event> events = compilationCreateDto.getEvents()
                .stream()
                .map(eventPrivateService::getEventById)
                .collect(Collectors.toList());
        return Compilation.builder()
                .title(compilationCreateDto.getTitle())
                .pinned(compilationCreateDto.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events)
                .build();
    }
}
