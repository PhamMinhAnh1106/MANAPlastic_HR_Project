package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "requirementrules")
public class RequirementrulesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruleID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "requirementID", nullable = false)
    private com.manaplastic.backend.entity.SchedulerequirementEntity requirementID;

    @NotNull
    @Column(name = "requiredskillGrade", nullable = false)
    private Integer requiredSkillgrade;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "minstaffcount", nullable = false)
    private Integer minStaffCount;



}