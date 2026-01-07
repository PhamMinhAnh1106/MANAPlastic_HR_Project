package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "contractfilehistory")
public class ContractfilehistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "contractId", nullable = false)
    private ContractEntity contract;

    @Size(max = 255)
    @NotNull
    @Column(name = "oldFileUrl", nullable = false)
    private String oldFileUrl;

    @Column(name = "archivedAt")
    private LocalDateTime archivedAt = LocalDateTime.now();

    @Lob
    @Column(name = "reasonForChange")
    private String reasonForChange;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "replacedByRequestId")
    private ContractchangerequestEntity replacedByRequest;

}