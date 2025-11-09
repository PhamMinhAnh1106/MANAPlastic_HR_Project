package com.manaplastic.backend.DTO;

public record RequirementRuleDTO(
        Integer ruleId,
        Integer requiredSkillGrade,
        Integer minStaffCount
) {
}
