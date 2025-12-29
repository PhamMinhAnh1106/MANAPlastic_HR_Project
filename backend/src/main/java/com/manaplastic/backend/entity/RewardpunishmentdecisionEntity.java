package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rewardpunishmentdecisions")
public class RewardpunishmentdecisionEntity {

    public enum RewardType { REWARD, PUNISHMENT }
    public enum DecisionStatus { PENDING, APPROVED, PROCESSED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DecisionID")
    private Integer id;

    @Column(name = "UserID", nullable = false)
    private Integer userID;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    private RewardType type;

    @Column(name = "Reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "DecisionDate", nullable = false)
    private LocalDate decisionDate;

    @Column(name = "Amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "IsTaxExempt")
    private Boolean isTaxExempt; // 1 = Miễn thuế, 0 = Chịu thuế

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private DecisionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", insertable = false, updatable = false)
    private UserEntity user;

}