package ru.practicum.explorewithme.event;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;

import java.time.LocalDateTime;
import java.util.*;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("select e from Event as e " +
            "where ((:text) is null " +
            "or upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%'))) " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and ((:paid) is null or e.paid = :paid) " +
            "and (e.eventDate >= :rangeStart) " +
            "and (CAST(:rangeEnd as date) is null or e.eventDate <= :rangeEnd)")
    List<Event> findEventsPublic(String text,
                                 List<Long> categories,
                                 Boolean paid,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd);

    @Query("select e from Event as e " +
            "where ((:users) is null or e.initiator.id in :users) " +
            "and ((:states) is null or e.state in :states) " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and (e.eventDate >= :rangeStart) " +
            "and (CAST(:rangeEnd as date) is null or e.eventDate <= :rangeEnd)")
    List<Event> getEventsByAdmin(List<Long> users,
                                 List<EventState> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Pageable pageable);

}