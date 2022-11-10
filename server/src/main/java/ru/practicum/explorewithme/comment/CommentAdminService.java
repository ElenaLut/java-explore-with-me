package ru.practicum.explorewithme.comment;

import ru.practicum.explorewithme.comment.dto.CommentDto;

import java.util.List;

public interface CommentAdminService {

    List<CommentDto> getAllComments(int from,
                                    int size);

    CommentDto updateStateComment(Long commentId, CommentDto commentDto);
}
