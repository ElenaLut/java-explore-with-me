package ru.practicum.explorewithme.compilation;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {

    List<CompilationDto> getAllCompilations(boolean pin, int from, int size);

    CompilationDto getFullCompilation(Long id);
}
