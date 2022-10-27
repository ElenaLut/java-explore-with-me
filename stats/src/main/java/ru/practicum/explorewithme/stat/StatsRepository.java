package ru.practicum.explorewithme.stat;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select " +
            "   new ru.practicum.explorewithme.stat.ViewStats(h.app, h.uri, cast((1) as int))" +
            "from EndpointHit as h " +
            "where h.timestamp >= :start " +
            "and h.timestamp <= :end " +
            "and (:uris is null or h.uri in :uris) " +
            "group by h.app, h.uri, case when (:unique = true) then h.ip else cast('ip' as string) end ")
    Collection<ViewStats> findAllByStartEndTime(LocalDateTime start,
                                                LocalDateTime end,
                                                @Param("uris") List<String> uris,
                                                @Param("unique") boolean unique);
}
