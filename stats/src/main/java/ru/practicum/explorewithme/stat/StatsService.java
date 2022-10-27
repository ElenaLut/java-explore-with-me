package ru.practicum.explorewithme.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public EndpointHit createHit(EndpointHit endpointHit) {
        return statsRepository.save(endpointHit);
    }

    public Collection<ViewStats> getHits(LocalDateTime start,
                                         LocalDateTime end,
                                         List<String> uris,
                                         Boolean unique) {
        return statsRepository.findAllByStartEndTime(start, end, uris, unique);
    }
}