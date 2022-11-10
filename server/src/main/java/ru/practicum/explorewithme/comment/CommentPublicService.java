package ru.practicum.explorewithme.comment;

import ru.practicum.explorewithme.comment.dto.CommentDto;

import java.util.List;

public interface CommentPublicService {

    List<CommentDto> findCommentsByEventId(Long eventId, int from, int size);
}
