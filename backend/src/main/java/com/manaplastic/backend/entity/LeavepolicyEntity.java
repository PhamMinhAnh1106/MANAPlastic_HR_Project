package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "leavepolicy")
public class LeavepolicyEntity {


    public enum LeaveType {ANNUAL, SICK, MATERNITY, PATERNITY}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyID", nullable = false)
    private Integer id;


    @NotNull
    @Column(name = "days", nullable = false)
    private Integer days;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Lob
    @Column(name = "leavetype", nullable = false)
    private LeaveType leavetype;

    @NotNull
    @Column(name = "minyearsservice", nullable = false)
    private Integer minyearsservice;

    @Column(name = "maxyearsservice")
    private Integer maxyearsservice;

    @Size(max = 100)
    @Column(name = "jobtype", length = 100)
    private String jobtype;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "leavetypeid")
    private ShiftEntity leavetypeid;

}