package com.manaplastic.backend.DTO.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequirementDTO {
        Integer requirementId;
        Integer departmentId;
        Integer shiftId;
        Integer totalStaffNeeded;
        List<RequirementRuleDTO> rules;
}
