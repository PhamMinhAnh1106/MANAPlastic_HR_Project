package com.manaplastic.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleID", nullable = false)
    private Integer id;

    @Column(name = "rolename", nullable = false, length = 100)
    private String rolename;

}