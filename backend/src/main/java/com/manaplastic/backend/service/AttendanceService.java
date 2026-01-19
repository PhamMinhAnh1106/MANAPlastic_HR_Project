package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.attendance.AttendanceDTO;
import com.manaplastic.backend.DTO.criteria.AttendanceFilterCriteria;
import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.exportfile.ExcelHelper;
import com.manaplastic.backend.filters.AttendanceFilter;
import com.manaplastic.backend.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    @Autowired
    private final AttendanceRepository attendanceRepository;

    // Lấy ds từ ộ loọc
//    public List<AttendanceDTO> getFilteredAttendance(AttendanceFilterCriteria criteria) {
//
//        Specification<AttendanceEntity> spec = AttendanceFilter.withCriteria(criteria);
//        List<AttendanceEntity> entities = attendanceRepository.findAll(spec);
//
//        //  Mapping sang DTO
//        return entities.stream()
//                .map(this::mapToAttendanceDTO)
//                .collect(Collectors.toList());
//    }
    public Page<AttendanceDTO> getFilteredAttendance(AttendanceFilterCriteria criteria, Pageable pageable) {
        Specification<AttendanceEntity> spec = AttendanceFilter.withCriteria(criteria);
        Page<AttendanceEntity> result = attendanceRepository.findAll(spec,pageable);
        return result.map(this::mapToAttendanceDTO);

    }
    //xóa data
    public void deleteAttendance(int attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }

    //Hàm Map
    private AttendanceDTO mapToAttendanceDTO(AttendanceEntity entity) {
        java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();// Intanst = datetime trong MySQL, là UTC

        return AttendanceDTO.builder()
                .attendanceId(entity.getId())
                .userName(entity.getUserID().getUsername())
                .fullNameUser(entity.getUserID().getFullname())
                .departmentName((entity.getUserID() != null && entity.getUserID().getDepartmentID() != null)
                        ? entity.getUserID().getDepartmentID().getDepartmentname()
                        : "Chưa có phòng ban")
                .attendanceDate(String.valueOf(entity.getDate()))
                .checkIn(entity.getCheckin() != null
                        ? entity.getCheckin().atZone(zoneId).toLocalDateTime()
                        : null)

                .checkOut(entity.getCheckout() != null
                        ? entity.getCheckout().atZone(zoneId).toLocalDateTime()
                        : null)

                .checkInImg(entity.getCheckinImgUrl())
                .checkOutImg(entity.getCheckoutImgUrl())
                .shiftId(entity.getShiftID() != null ? entity.getShiftID().getId() : null)
                .shiftName(entity.getShiftID() != null ? entity.getShiftID().getShiftname() : "Trống")
                .status(String.valueOf(entity.getStatus()))
                .estimatedSalary(entity.getEstimatedSalary())
                .build();
    }

    //Xuất file
    public ByteArrayInputStream exportReport(AttendanceFilterCriteria criteria) {
        Specification<AttendanceEntity> spec = AttendanceFilter.withCriteria(criteria);
        List<AttendanceEntity> entities = attendanceRepository.findAll(spec);

        List<AttendanceDTO> dtos = entities.stream()
                .map(this::mapToAttendanceDTO)
                .collect(Collectors.toList());

        return ExcelHelper.attendanceToExcel(dtos);
    }
}
