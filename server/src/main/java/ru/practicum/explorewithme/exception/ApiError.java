package ru.practicum.explorewithme.exception;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ApiError {
    private Set<String> errors = new HashSet<>();
    private String message;
    private String reason;
    private String status;
    private Timestamp timestamp;
}
