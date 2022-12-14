package ru.practicum.explorewithme.event.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Float lat;
    private Float lon;
}
