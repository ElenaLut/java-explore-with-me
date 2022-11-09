package ru.practicum.explorewithme.comment;

import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.ShortCommentDto;

import java.util.List;

public interface CommentPrivateService {

    List<CommentDto> getCommentsOfAuthor(Long userId, int from, int size);

    CommentDto changeCommentByAuthor(ShortCommentDto updateCommentDto, Long userId, Long commentId);

    CommentDto createComment(ShortCommentDto newCommentDto, Long userId);

    void deleteComment(Long commentId, Long userId);
}
