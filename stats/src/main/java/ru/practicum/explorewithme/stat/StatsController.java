package ru.practicum.explorewithme.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHit createHit(@RequestBody @Valid EndpointHit endpointHit) {
        log.info("Create hit {}", endpointHit);
        return statsService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Get hits start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getHits(start, end, uris, unique);
    }
}