package ru.practicum.explorewithme.event.dto;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Float lat;
    private Float lon;
}
