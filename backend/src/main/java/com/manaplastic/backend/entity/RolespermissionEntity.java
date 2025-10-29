package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "rolespermissions")
public class RolespermissionEntity {
    @EmbeddedId
    private RolespermissionEntityId id;

    @MapsId("roleID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "roleID", nullable = false)
    private RoleEntity roleID;

    @MapsId("permissionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "permissionID", nullable = false)
    private PermissionEntity permissionID;

}