package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public Comment toCommentFromUpdate(UpdateCommentDto newCommentDto) {
        return Comment.builder()
                .description(newCommentDto.getDescription())
                .created(LocalDateTime.now())
                .build();

    }

    public Comment toCommentFromNew(NewCommentDto newCommentDto, Event event, User user) {
        return Comment.builder()
                .description(newCommentDto.getDescription())
                .created(LocalDateTime.now())
                .event(event)
                .user(user)
                .status(CommentState.PENDING)
                .build();

    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .created(comment.getCreated())
                .event(comment.getEvent().getId())
                .user(comment.getUser().getId())
                .status(comment.getStatus())
                .build();
    }
}
