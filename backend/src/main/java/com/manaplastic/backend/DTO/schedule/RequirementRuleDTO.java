package com.manaplastic.backend.DTO.schedule;

public record RequirementRuleDTO(
        Integer ruleId,
        Integer requiredSkillGrade,
        Integer minStaffCount
) {
}
