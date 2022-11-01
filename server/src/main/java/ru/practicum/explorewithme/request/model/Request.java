package ru.practicum.explorewithme.request.model;

import lombok.*;
import ru.practicum.explorewithme.request.dto.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @JoinColumn(name = "event_id")
    @Column(name = "created")
    private LocalDateTime created;
    @NotNull
    private Long eventId;
    @NotNull
    @JoinColumn(name = "requester_id")
    private Long requesterId;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;
}
