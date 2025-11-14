package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "leavebalance")
public class LeavebalanceEntity {
    @EmbeddedId
    private LeavebalanceEntityId id;

    @MapsId("userID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity userID;

    @MapsId("leaveTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private ShiftEntity leaveType;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_granted", nullable = false)
    private Integer totalGranted;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "carried_over", nullable = false)
    private Integer carriedOver;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "days_used", nullable = false)
    private Integer daysUsed;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_updated")
    private Instant lastUpdated;

}