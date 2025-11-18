package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dependents")
public class DependentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependentID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity userID;

    @Size(max = 255)
    @NotNull
    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Size(max = 100)
    @NotNull
    @Column(name = "relationship", nullable = false, length = 100)
    private String relationship;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "gender")
    private Boolean gender;

    @Size(max = 20)
    @Column(name = "idcardnumber", length = 20)
    private String idCardNumber;

    @Size(max = 20)
    @Column(name = "phonenumber", length = 20)
    private String phonenumber;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "istaxdeductible", nullable = false)
    private Boolean isTaxDeductible = false;


}