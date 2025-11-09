package com.manaplastic.backend.DTO;

import java.util.List;

public record ScheduleRequirementDTO (
        Integer requirementId,
        Integer departmentId,
        Integer shiftId,
        Integer totalStaffNeeded,
        List<RequirementRuleDTO> rules
) {
}
