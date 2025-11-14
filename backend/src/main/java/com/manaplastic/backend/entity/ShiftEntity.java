package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @Transient // Đánh dấu để JPA biết không map cột này vào DB
    public LeavepolicyEntity.LeaveType getShiftnameAsEnum() { // Chuyển Shiftname (String) sang LeaveType (Enum) để so sánh
        if (this.shiftname == null) {
            return null;
        }
        if (this.shiftname.startsWith("AL")) {
            return LeavepolicyEntity.LeaveType.ANNUAL;
        }
        if (this.shiftname.startsWith("SL")) {
            return LeavepolicyEntity.LeaveType.SICK;
        }
        if (this.shiftname.startsWith("PL")) {
            // Giả định PL (Personal Leave) dùng chung chính sách PATERNITY
            return LeavepolicyEntity.LeaveType.PATERNITY;
        }
        if (this.shiftname.startsWith("ML")) {
            return LeavepolicyEntity.LeaveType.MATERNITY;
        }

        return null; // nếu ca làm việc là C808,C809,...
    }
}