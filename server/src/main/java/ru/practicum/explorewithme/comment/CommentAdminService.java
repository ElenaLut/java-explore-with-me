package ru.practicum.explorewithme.comment;

import ru.practicum.explorewithme.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentAdminService {

    List<CommentDto> getAllComments(Long userId,
                                    Long eventId,
                                    String state,
                                    LocalDateTime start,
                                    LocalDateTime end,
                                    int from,
                                    int size);

    CommentDto updateStateComment(Long commentId, CommentDto commentDto);
}
