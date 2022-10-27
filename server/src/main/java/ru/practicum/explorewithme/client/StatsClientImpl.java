package ru.practicum.explorewithme.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class StatsClientImpl implements StatsClient {
    private final RestTemplate restTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    @Override
    public void hit(EndpointHit endpointHit) {
        log.info("Hit this {} server {}", endpointHit, restTemplate.getUriTemplateHandler());
        restTemplate.postForObject("/hit", endpointHit, String.class);
    }

    @Override
    public List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Stats start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        StringBuilder builder = new StringBuilder(String.format("/stats?start=%s&end=%s",
                start.format(formatter),
                end.format(formatter)));
        if (uris != null) {
            builder.append(String.format("&uris=%s", String.join(",", uris)));
        }
        if (unique != null) {
            builder.append(String.format("&unique=%s", unique));
        }
        return restTemplate.getForObject(builder.toString(), List.class);
    }
}
