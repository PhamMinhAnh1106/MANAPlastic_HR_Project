package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "leavepolicy")
public class LeavepolicyEntity {
    public enum LeaveType { ANNUAL, SICK, MATERNITY, PATERNITY }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyID", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @NotNull
    @Column(name = "min_years_service", nullable = false)
    private Integer minYearsService;

    @Column(name = "max_years_service")
    private Integer maxYearsService;

    @Size(max = 100)
    @Column(name = "job_type", length = 100)
    private String jobType;

    @NotNull
    @Column(name = "days", nullable = false)
    private Integer days;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

}