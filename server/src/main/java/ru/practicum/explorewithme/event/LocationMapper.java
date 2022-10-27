package ru.practicum.explorewithme.event;

import lombok.*;
import ru.practicum.explorewithme.event.dto.LocationDto;
import ru.practicum.explorewithme.event.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}