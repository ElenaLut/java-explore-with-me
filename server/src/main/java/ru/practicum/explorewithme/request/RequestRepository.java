package ru.practicum.explorewithme.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "select count(*) " +
            "from requests " +
            "where event_id = ?1 " +
            "and status = 'CONFIRMED'",
            nativeQuery = true)
    Integer getConfirmedRequests(Long eventId);

    @Modifying
    @Query("update Request as r " +
            "set r.status = 'REJECTED' " +
            "where r.status = 'PENDING' " +
            "and r.eventId = ?1")
    void rejectPendingRequests(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);
}
