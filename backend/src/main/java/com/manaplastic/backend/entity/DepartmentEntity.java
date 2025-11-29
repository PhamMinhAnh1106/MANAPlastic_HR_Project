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
@Table(name = "departments")
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departmentID", nullable = false)
    private Integer id;

    @Column(name = "departmentname", nullable = false)
    private String departmentname;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "managerID")
    private com.manaplastic.backend.entity.UserEntity managerID;

    @ColumnDefault("b'0'")
    @Column(name = "isoffice")
    private Boolean isoffice;

}