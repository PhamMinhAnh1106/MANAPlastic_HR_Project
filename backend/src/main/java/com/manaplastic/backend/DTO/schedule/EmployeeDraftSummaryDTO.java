package com.manaplastic.backend.DTO.schedule;

import java.util.List;

public record EmployeeDraftSummaryDTO (
        Integer employeeId,
        String employeeFullName,
        List<DraftRegistrationDTO> drafts //Tái sử dụng lại DTO cũ
) {
}
