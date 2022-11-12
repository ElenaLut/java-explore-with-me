package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.user.UserRepository;
import ru.practicum.explorewithme.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentPrivateServiceImpl implements CommentPrivateService {

    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId) {
        Event event = eventRepository.findById(newCommentDto.getEvent()).orElseThrow(() ->
                new NotFoundException("Не найдено событие с id " + newCommentDto.getEvent()));
        User user = getUserById(userId);
        Comment comment = commentMapper.toCommentFromNew(newCommentDto, event, user);
        log.info("Создан комментарий {}", comment);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto changeCommentByAuthor(UpdateCommentDto updateCommentDto, Long userId, Long commentId) {
        Comment oldComment = getCommentById(commentId);
        if (!oldComment.getUser().getId().equals(userId)) {
            log.error("Пользователь {} не является автором комментария {}", userId, oldComment.getId());
            throw new ForbiddenException("Комментарий может обновить только автор");
        }
        Comment updateComment = commentMapper.toCommentFromUpdate(updateCommentDto);
        Optional.ofNullable(updateComment.getDescription()).ifPresent(oldComment::setDescription);
        oldComment.setStatus(CommentState.PENDING);
        log.info("Комментарий {} обновлен", oldComment);
        return commentMapper.toCommentDto(commentRepository.save(oldComment));
    }

    @Override
    public void cancelComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            log.error("Пользователь {} не является автором комментария {}", userId, commentId);
            throw new ForbiddenException("Только автор комментарий может его удалить");
        }
        comment.setStatus(CommentState.CANCELED);
        log.info("Комментарий {} удален", comment);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getCommentsOfAuthor(Long userId, int from, int size) {
        getUserById(userId);
        return commentRepository.findAllByUserId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с id " + id));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + id));
    }
}