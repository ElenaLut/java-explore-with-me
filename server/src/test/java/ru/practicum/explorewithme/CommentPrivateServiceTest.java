package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.comment.CommentMapper;
import ru.practicum.explorewithme.comment.CommentPrivateService;
import ru.practicum.explorewithme.comment.dto.CommentDto;
import ru.practicum.explorewithme.comment.dto.NewCommentDto;
import ru.practicum.explorewithme.comment.dto.UpdateCommentDto;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.exception.ForbiddenException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentPrivateServiceTest {

    @Autowired
    private final CommentPrivateService service;

    @Autowired
    private final CommentMapper mapper;

    @Autowired
    private final TestEntityManager testEntityManager;

    private static AtomicLong userIdHolder;
    private static AtomicLong eventIdHolder;

    @BeforeAll
    public static void init() {
        userIdHolder = new AtomicLong();
        eventIdHolder = new AtomicLong();
    }

    @BeforeEach
    public void beforeEachCommentServiceTests() {
        testEntityManager.clear();
    }

    @Test
    public void createCommentOkTest() {
        Event event = generateEvent();
        User user = generateUser();
        NewCommentDto newComment = NewCommentDto.builder()
                .description("Comment")
                .event(event.getId())
                .build();
        CommentDto saved = service.createComment(newComment, user.getId());
        CommentDto actual = mapper.toCommentDto(testEntityManager.find(Comment.class, saved.getId()));
        assertEquals(saved.getDescription(), actual.getDescription());
    }

    @Test
    public void createCommentWithWrongEventIdTest() {
        User user = generateUser();
        NewCommentDto newComment = NewCommentDto.builder()
                .description("Comment")
                .event(78L)
                .build();
        assertThrows(NotFoundException.class,
                () -> service.createComment(newComment, user.getId()));
    }

    @Test
    public void updateCommentOkTest() {
        Event event = generateEvent();
        User user = generateUser();
        NewCommentDto newComment = NewCommentDto.builder()
                .description("Comment")
                .event(event.getId())
                .build();
        CommentDto saved = service.createComment(newComment, user.getId());
        UpdateCommentDto updateComment = UpdateCommentDto.builder()
                .description("CommentUpdate")
                .build();
        CommentDto updated = service.changeCommentByAuthor(updateComment, user.getId(), saved.getId());
        CommentDto actual = mapper.toCommentDto(testEntityManager.find(Comment.class, updated.getId()));
        assertEquals(updated.getDescription(), actual.getDescription());
    }

    @Test
    public void updateCommentWithWrongCommentIdTest() {
        User user = generateUser();
        UpdateCommentDto updateComment = UpdateCommentDto.builder()
                .description("CommentUpdate")
                .build();
        assertThrows(NotFoundException.class,
                () -> service.changeCommentByAuthor(updateComment, user.getId(), 78L));
    }

    @Test
    public void deleteCommentOkTest() {
        Comment comment = generateComment();
        service.cancelComment(comment.getId(), comment.getUser().getId());
        Comment deletedComment = comment;
        deletedComment.setStatus(CommentState.CANCELED);
        assertEquals(testEntityManager.find(Comment.class, comment.getId()), deletedComment);
    }

    @Test
    public void deleteCommentByWrongUserTest() {
        Comment comment = generateComment();
        assertThrows(ForbiddenException.class,
                () -> service.cancelComment(comment.getId(), 78L));
    }

    @Test
    public void getCommentsOkTest() {
        Event event = generateEvent();
        User user = generateUser();
        NewCommentDto newComment = NewCommentDto.builder()
                .description("Comment")
                .event(event.getId())
                .build();
        CommentDto commentDto = service.createComment(newComment, user.getId());
        List<CommentDto> commentsOfUser = List.of(commentDto);
        List<CommentDto> commentDtoList = service.getCommentsOfAuthor(user.getId(), 0, 10);
        assertEquals(commentsOfUser, commentDtoList);
    }

    private User generateUser() {
        User user = User.builder()
                .name(String.format("user-%s", userIdHolder.incrementAndGet()))
                .email(String.format("email-%s@yandex.ru", userIdHolder.get()))
                .build();
        return testEntityManager.persist(user);
    }

    private Event generateEvent() {
        Event event = Event.builder()
                .title(String.format("title-%s", eventIdHolder.incrementAndGet()))
                .annotation(String.format("annotation-%s", eventIdHolder.get()))
                .description(String.format("description-%s", eventIdHolder.get()))
                .category(generateCategory("Category"))
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.now().plusMonths(1))
                .paid(true)
                .initiator(generateUser())
                .participantLimit(800)
                .requestModeration(false)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .state(EventState.PUBLISHED)
                .build();
        return testEntityManager.persist(event);
    }

    private Comment generateComment() {
        Comment comment = Comment.builder()
                .description("Comment " + System.nanoTime())
                .event(generateEvent())
                .user(generateUser())
                .created(LocalDateTime.now())
                .status(CommentState.PUBLISHED)
                .build();
        return testEntityManager.persist(comment);
    }

    private Category generateCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .build();
        return testEntityManager.persistAndFlush(category);
    }
}
