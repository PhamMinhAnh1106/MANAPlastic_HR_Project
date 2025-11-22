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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DecisionID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "UserID", nullable = false)
    private UserEntity userID;

    @NotNull
    @Lob
    @Column(name = "Type", nullable = false)
    private String type;

    @NotNull
    @Lob
    @Column(name = "Reason", nullable = false)
    private String reason;

    @NotNull
    @Column(name = "DecisionDate", nullable = false)
    private LocalDate decisionDate;

    @ColumnDefault("0.00")
    @Column(name = "Amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @ColumnDefault("0")
    @Column(name = "IsTaxExempt")
    private Boolean isTaxExempt;

    @ColumnDefault("'PENDING'")
    @Lob
    @Column(name = "Status")
    private String status;

}