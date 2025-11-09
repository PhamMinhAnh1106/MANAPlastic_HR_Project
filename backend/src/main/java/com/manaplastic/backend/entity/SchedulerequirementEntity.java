package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "schedulerequirements")
public class SchedulerequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirementID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "departmentID", nullable = false)
    private DepartmentEntity departmentID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shiftID", nullable = false)
    private ShiftEntity shiftID;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_staff_needed", nullable = false)
    private Integer totalStaffNeeded;

    @OneToMany(mappedBy = "requirementID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequirementrulesEntity> rules;
}