package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.model.CommentState;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentPublicServiceImpl implements CommentPublicService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> findCommentsByEventId(Long eventId, int from, int size) {
        log.info("Получение списка отзывов для события {}.", eventId);
        return commentRepository.findAllByEventIdAndStatus(eventId, CommentState.PUBLISHED, PageRequest.of(from / size, size))
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
