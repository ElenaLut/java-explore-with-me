package ru.practicum.explorewithme.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(Long userId, Pageable pageable);

    List<Comment> findAllByEventIdAndStatus(Long eventId, CommentState status);

    @Query("select c from Comment as c " +
            "where ((:userId) is null or c.user.id = :userId) " +
            "and ((:eventId) is null or c.event.id = :eventId) " +
            "and ((:state) is null or c.status = :state) " +
            "and ((:rangeStart) is null or c.created >= :rangeStart) " +
            "and ((:rangeEnd) is null or c.created <= :rangeEnd)")
    List<Comment> getCommentsByAdmin(Long userId,
                                     Long eventId,
                                     String state,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Pageable pageable);

}
