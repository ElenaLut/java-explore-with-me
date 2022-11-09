package ru.practicum.explorewithme.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.ShortCommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;

    @GetMapping("/{userId}/comments")
    public List<CommentDto> getCommentsOfAuthor(@PathVariable Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        return commentPrivateService.getCommentsOfAuthor(userId, from, size);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto changeCommentByAuthor(@Valid @RequestBody ShortCommentDto updateCommentDto,
                                            @PathVariable Long userId,
                                            @PathVariable Long commentId) {
        return commentPrivateService.changeCommentByAuthor(updateCommentDto, userId, commentId);
    }

    @PostMapping("/{userId}/comments")
    public CommentDto createComment(@Valid @RequestBody ShortCommentDto newCommentDto,
                                    @PathVariable Long userId) {
        return commentPrivateService.createComment(newCommentDto, userId);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              @PathVariable Long userId) {
        commentPrivateService.deleteComment(commentId, userId);
    }
}
