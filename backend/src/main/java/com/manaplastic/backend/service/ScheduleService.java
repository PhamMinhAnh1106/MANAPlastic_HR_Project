package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.DraftRegistrationDTO;
import com.manaplastic.backend.entity.EmployeedraftscheduleEntity;
import com.manaplastic.backend.entity.ShiftEntity;
import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.EmployeeDraftScheduleRepository;
import com.manaplastic.backend.repository.ShiftRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động inject (tiêm) Repository
public class ScheduleService {
    private final EmployeeDraftScheduleRepository draftRepository;
    private final UserRepository userRepository; // <-- Cần cho Lỗi 4
    private final ShiftRepository shiftRepository; // <-- Cần cho Lỗi 3

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public void registerDraftSchedule(List<DraftRegistrationDTO> dtos, Integer employeeId) {

        // SỬA LỖI 4: Lấy đối tượng UserEntity một lần bên ngoài vòng lặp
        UserEntity employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));

        List<EmployeedraftscheduleEntity> schedulesToSave = new ArrayList<>();

        for (DraftRegistrationDTO dto : dtos) {
            EmployeedraftscheduleEntity draft = draftRepository.findByEmployeeIDAndDate(employee, dto.date())
                    .orElse(new EmployeedraftscheduleEntity());

            draft.setEmployeeID(employee);
            draft.setDate(dto.date());

            if (dto.shiftId() != null) {
                ShiftEntity shift = shiftRepository.findById(dto.shiftId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.shiftId()));
                draft.setShiftID(shift);
            } else {
                draft.setShiftID(null); // Cho phép để trống (NULL)
            }

            draft.setIsDayOff(dto.isDayOff());
            draft.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
            draft.setRegistrationDate(Instant.now());

            schedulesToSave.add(draft);
        }

        draftRepository.saveAll(schedulesToSave);
    }

    public List<DraftRegistrationDTO> getDraftSchedule(Integer employeeId, String monthYear) {

        UserEntity employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));

        List<EmployeedraftscheduleEntity> drafts = draftRepository.findByEmployeeIDAndMonthYear(employee, monthYear);

        return drafts.stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }


    private DraftRegistrationDTO mapEntityToDTO(EmployeedraftscheduleEntity entity) {
        Integer shiftId = null;
        if (entity.getShiftID() != null) {
            shiftId = entity.getShiftID().getId();
        }

        return new DraftRegistrationDTO(
                entity.getDate(),
                shiftId,
                entity.getIsDayOff()
        );
    }
}
