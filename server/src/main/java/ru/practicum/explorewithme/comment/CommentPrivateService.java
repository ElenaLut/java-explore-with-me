package ru.practicum.explorewithme.comment;

import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentPrivateService {

    List<CommentDto> getCommentsOfAuthor(Long userId, int from, int size);

    CommentDto changeCommentByAuthor(UpdateCommentDto updateCommentDto, Long userId, Long commentId);

    CommentDto createComment(NewCommentDto newCommentDto, Long userId);

    void cancelComment(Long commentId, Long userId);
}
