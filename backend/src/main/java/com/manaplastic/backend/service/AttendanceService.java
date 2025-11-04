package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.AttendanceDTO;
import com.manaplastic.backend.DTO.AttendanceFilterCriteria;
import com.manaplastic.backend.entity.AttendanceEntity;
import com.manaplastic.backend.filters.AttendanceFilter;
import com.manaplastic.backend.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    @Autowired
    private final AttendanceRepository attendanceRepository;


    public List<AttendanceDTO> getFilteredAttendance(AttendanceFilterCriteria criteria) {

        Specification<AttendanceEntity> spec = AttendanceFilter.withCriteria(criteria);
        List<AttendanceEntity> entities = attendanceRepository.findAll(spec);

        //  Mapping sang DTO
        return entities.stream()
                .map(this::mapToAttendanceDTO)
                .collect(Collectors.toList());
    }

    public void deleteAttendance(int attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }

    private AttendanceDTO mapToAttendanceDTO(AttendanceEntity entity) {
        java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();// Intanst = datetime trong MySQL, là UTC

        return AttendanceDTO.builder()
                .attendanceId(entity.getId())
                .attendanceDate(String.valueOf(entity.getDate()))
                .checkIn(entity.getCheckin() != null
                        ? entity.getCheckin().atZone(zoneId).toLocalDate()
                        : null)

                .checkOut(entity.getCheckout() != null
                        ? entity.getCheckout().atZone(zoneId).toLocalDate()
                        : null)

                .checkInImg(entity.getCheckinImgUrl())
                .checkOutImg(entity.getCheckoutImgUrl())
                .shiftId(entity.getShiftID() != null ? entity.getShiftID().getId() : null)
                .status(String.valueOf(entity.getStatus()))
                .build();
    }
}
