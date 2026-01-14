package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "contracts")
public class ContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contractID", nullable = false)
    private Integer id;

    @Column(name = "contractname", nullable = false)
    private String contractname;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "basesalary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basesalary;

    @Column(name = "fileurl")
    private String fileurl;

    @Column(name = "signdate")
    private LocalDate signdate;

    @Column(name = "startdate", nullable = false)
    private LocalDate startdate;

    @Column(name = "enddate")
    private LocalDate enddate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userID", nullable = false)
    private com.manaplastic.backend.entity.UserEntity userID;

    @ColumnDefault("0.00")
    @Column(name = "InsuranceSalary", precision = 15, scale = 2)
    private BigDecimal insuranceSalary;

    @ColumnDefault("'DRAFT'")
    @Lob
    @Column(name = "Status")
    private String status;


    @Size(max = 50)
    @NotNull
    @Column(name = "contractCode", nullable = false, length = 50)
    private String contractCode;

    @ColumnDefault("'FULLTIME'")
    @Lob
    @Column(name = "WorkType")
    private String workType;

    @ManyToOne
    @JoinColumn(name = "departmentID") // Trùng tên cột vừa tạo trong DB
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "roleID")      // Trùng tên cột vừa tạo trong DB
    private RoleEntity role;

    @ColumnDefault("100.00")
    @Column(name = "insurancePercent", precision = 5, scale = 4)
    private BigDecimal insurancePercent;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private ContractTemplateEntity contractTemplate;

    @ColumnDefault("8.0")
    @Column(name = "StandardHours", precision = 4, scale = 1)
    private BigDecimal standardHours;

}