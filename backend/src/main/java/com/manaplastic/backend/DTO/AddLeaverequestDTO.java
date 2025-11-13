package com.manaplastic.backend.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddLeaverequestDTO(
        @NotNull
        String leavetype,

        @NotNull
        @FutureOrPresent
        LocalDate startdate,

        @NotNull
        @FutureOrPresent
        LocalDate enddate,

        String reason
) {
}
