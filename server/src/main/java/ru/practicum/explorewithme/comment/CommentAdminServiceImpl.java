package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getAllComments(int from,
                                           int size) {
        return commentRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateStateComment(Long commentId, CommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        Optional.ofNullable(commentDto.getStatus()).ifPresent(comment::setStatus);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с id " + id));
    }
}
