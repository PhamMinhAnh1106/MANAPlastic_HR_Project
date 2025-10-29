package com.manaplastic.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permissionID", nullable = false)
    private Integer id;

    @Column(name = "permissionname", nullable = false)
    private String permissionname;

    @Lob
    @Column(name = "description")
    private String description;

}