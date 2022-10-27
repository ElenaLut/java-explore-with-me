package ru.practicum.explorewithme.client;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void hit(EndpointHit endpointHit);

    List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
