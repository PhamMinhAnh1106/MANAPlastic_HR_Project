package com.manaplastic.backend.entity;

import com.manaplastic.backend.constant.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", nullable = false)
    private Integer id;

    @Column(name = "username" ,length = 100)
    private String username;

    @Column(name = "password",length = 100)
    private String password;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "cccd", nullable = true)
    private String cccd;

    @Column(name = "email")
    private String email;

    @Column(name = "phonenumber", length = 20)
    private String phonenumber;

    @Column(name = "birth")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "bankaccount", length = 50)
    private String bankaccount;

    @ColumnDefault("1")
    @Column(name = "skillGrade")
    private Integer skillGrade;

    @Column(name = "bankname", length = 100)
    private String bankname;

    @Column(name = "hiredate")
    private LocalDate hiredate;

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleID")
    private RoleEntity roleID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departmentID")
    private DepartmentEntity departmentID;


    @Size(max = 100)
    @NotNull
    @ColumnDefault("'NORMAL'")
    @Column(name = "jobtype", nullable = false, length = 100)
    private String jobtype;

    @Size(max = 20)
    @Column(name = "taxCode", length = 20)
    private String taxCode;

    @Size(max = 20)
    @Column(name = "socialInsuranceNumber", length = 20)
    private String socialInsuranceNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.roleID.getRolename()));
    }

}