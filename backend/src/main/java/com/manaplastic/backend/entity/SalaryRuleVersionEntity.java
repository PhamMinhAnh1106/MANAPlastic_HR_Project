package com.manaplastic.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "salary_rule_version")
public class SalaryRuleVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id", nullable = false)
    private Integer id;

    @Column(name = "rule_id")
    private Integer ruleId;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "dsl_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> dslJson;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}