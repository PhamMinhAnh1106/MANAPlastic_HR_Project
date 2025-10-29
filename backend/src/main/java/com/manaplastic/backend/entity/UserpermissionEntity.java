package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "userpermissions")
public class UserpermissionEntity {
    @EmbeddedId
    private UserpermissionEntityId id;

    @MapsId("userID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private com.manaplastic.backend.entity.UserEntity userID;

    @MapsId("permissionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "permissionID", nullable = false)
    private PermissionEntity permissionID;

    @ColumnDefault("1")
    @Column(name = "activepermission")
    private Boolean activepermission;

}