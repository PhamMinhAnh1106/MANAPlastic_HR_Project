package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "salary_rule")
public class SalaryRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "rule_code", length = 100)
    private String ruleCode;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @ColumnDefault("'DRAFT'")
    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "current_version_id")
    private Integer currentVersionId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("10")
    @Column(name = "priority")
    private Integer priority;

}