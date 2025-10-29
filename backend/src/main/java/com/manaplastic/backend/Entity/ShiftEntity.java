package com.manaplastic.backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "shifts")
public class ShiftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shiftID", nullable = false)
    private Integer id;

    @Column(name = "shiftname", nullable = false, length = 100)
    private String shiftname;

    @Column(name = "starttime", nullable = false)
    private LocalTime starttime;

    @Column(name = "endtime", nullable = false)
    private LocalTime endtime;

}