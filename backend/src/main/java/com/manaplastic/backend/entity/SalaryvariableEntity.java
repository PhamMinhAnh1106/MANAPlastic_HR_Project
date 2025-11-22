package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "salaryvariables")
public class SalaryvariableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariableID", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "Code", nullable = false, length = 50)
    private String code;

    @Lob
    @Column(name = "Description")
    private String description;

    @Size(max = 50)
    @Column(name = "ExampleValue", length = 50)
    private String exampleValue;

}