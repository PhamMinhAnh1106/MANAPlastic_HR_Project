package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.schedule.*;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final EmployeeDraftScheduleRepository draftRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeOfficialScheduleRepository officialRepository;

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    //Nhân viên
    //Lịch nháp - đk của nhân viên
    @Transactional
    public void registerDraftSchedule(List<DraftRegistrationDTO> dtos, Integer employeeId) {

        UserEntity employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));

        List<EmployeedraftscheduleEntity> schedulesToSave = new ArrayList<>();
        List<EmployeedraftscheduleEntity> schedulesToDelete = new ArrayList<>();

        for (DraftRegistrationDTO dto : dtos) {

            Optional<EmployeedraftscheduleEntity> existingDraftOpt = draftRepository.findByEmployeeIDAndDate(employee, dto.date());
            boolean isActiveChoice = (dto.shiftId() != null || dto.isDayOff());

            if (isActiveChoice) {
                EmployeedraftscheduleEntity draft = existingDraftOpt.orElse(new EmployeedraftscheduleEntity());
                draft.setEmployeeID(employee);
                draft.setDate(dto.date());

                if (dto.shiftId() != null) {
                    ShiftEntity shift = shiftRepository.findById(dto.shiftId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.shiftId()));
                    draft.setShiftID(shift);
                } else {
                    draft.setShiftID(null);
                }

                draft.setIsDayOff(dto.isDayOff());
                draft.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                draft.setRegistrationDate(Instant.now());
                schedulesToSave.add(draft);

            } else {
                existingDraftOpt.ifPresent(schedulesToDelete::add);
            }
        }

        if (!schedulesToSave.isEmpty()) {
            draftRepository.saveAll(schedulesToSave);
        }
        if (!schedulesToDelete.isEmpty()) {
            draftRepository.deleteAll(schedulesToDelete);
        }
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
        String shiftName = null;
        if (entity.getShiftID() != null) {
            shiftId = entity.getShiftID().getId();
            shiftName = entity.getShiftID().getShiftname();
        }
        return new DraftRegistrationDTO(
                entity.getDate(),
                shiftId,
                shiftName,
                entity.getIsDayOff()
        );
    }

    // Lịch chính thức
    public List<DraftRegistrationDTO> getOfficialSchedule(Integer employeeId, String monthYear) {
        UserEntity employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));
        List<EmployeeofficialscheduleEntity> officialSchedules = officialRepository.findByEmployeeIDAndMonthYear(employee, monthYear);
        return officialSchedules.stream()
                .map(this::mapOfficialEntityToDTO)
                .collect(Collectors.toList());
    }

    private DraftRegistrationDTO mapOfficialEntityToDTO(EmployeeofficialscheduleEntity entity) {
        Integer shiftId = null;
        String shiftName = null;
        if (entity.getShiftID() != null) {
            shiftId = entity.getShiftID().getId();
            shiftName = entity.getShiftID().getShiftname();
        }
        return new DraftRegistrationDTO(
                entity.getDate(),
                shiftId,
                shiftName,
                entity.getIsDayOff()
        );
    }

    // Quản lý
    // Lấy TẤT CẢ lịch nháp của nhân viên trong phòng ban
    public List<EmployeeDraftSummaryDTO> getDepartmentDraftSchedules(Integer managerId, String monthYear) {
        UserEntity manager = getUserOrThrow(managerId);
        if (manager.getDepartmentID() == null) {
            throw new RuntimeException("Manager không được gán vào phòng ban nào.");
        }
        DepartmentEntity departmentId = manager.getDepartmentID().getManagerID().getDepartmentID();
        List<UserEntity> employeesInDept = userRepository.findByDepartmentID(departmentId);
        List<EmployeedraftscheduleEntity> drafts = draftRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        Map<Integer, List<DraftRegistrationDTO>> draftsByEmployeeId = drafts.stream()
                .collect(Collectors.groupingBy(
                        draft -> draft.getEmployeeID().getId(),
                        Collectors.mapping(this::mapEntityToDTO, Collectors.toList())
                ));

        return employeesInDept.stream()
                .map(employee -> new EmployeeDraftSummaryDTO(
                        employee.getId(),
                        employee.getFullname(),
                        draftsByEmployeeId.getOrDefault(employee.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    // Cập nhật/Sửa hàng loạt các bản nháp (thay đổi ca, xóa ca)
    @Transactional
    public void updateDraftScheduleBatch(List<ManagerDraftUpdateDTO> dtos, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        Integer departmentId = manager.getDepartmentID().getId();

        List<EmployeedraftscheduleEntity> schedulesToSave = new ArrayList<>();
        List<EmployeedraftscheduleEntity> schedulesToDelete = new ArrayList<>();

        for (ManagerDraftUpdateDTO dto : dtos) {
            UserEntity employee = getUserOrThrow(dto.employeeId());

            if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
                throw new SecurityException("Manager không có quyền chỉnh sửa lịch của nhân viên: " + employee.getFullname());
            }

            Optional<EmployeedraftscheduleEntity> existingDraftOpt = draftRepository.findByEmployeeIDAndDate(employee, dto.date());
            boolean isActiveChoice = (dto.shiftId() != null || dto.isDayOff());

            if (isActiveChoice) {
                EmployeedraftscheduleEntity draft = existingDraftOpt.orElse(new EmployeedraftscheduleEntity());
                draft.setEmployeeID(employee);
                draft.setDate(dto.date());
                draft.setShiftID(dto.shiftId() != null ? getShiftOrThrow(dto.shiftId()) : null);
                draft.setIsDayOff(dto.isDayOff());
                draft.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                draft.setRegistrationDate(Instant.now());
                schedulesToSave.add(draft);
            } else {
                existingDraftOpt.ifPresent(schedulesToDelete::add);
            }
        }

        if (!schedulesToSave.isEmpty()) draftRepository.saveAll(schedulesToSave);
        if (!schedulesToDelete.isEmpty()) draftRepository.deleteAll(schedulesToDelete);
    }

    // "HOÀN TẤT" Lịch
    @Transactional
    public void finalizeSchedule(String monthYear, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        DepartmentEntity departmentId = manager.getDepartmentID().getManagerID().getDepartmentID();
        List<UserEntity> employeesInDept = userRepository.findByDepartmentID(departmentId);

        List<EmployeedraftscheduleEntity> draftsToFinalize = draftRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);
        officialRepository.deleteByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        List<EmployeeofficialscheduleEntity> officialSchedules = draftsToFinalize.stream()
                .map(draft -> {
                    EmployeeofficialscheduleEntity official = new EmployeeofficialscheduleEntity();
                    official.setEmployeeID(draft.getEmployeeID());
                    official.setDate(draft.getDate());
                    official.setShiftID(draft.getShiftID());
                    official.setIsDayOff(draft.getIsDayOff());
                    official.setMonthYear(draft.getMonthYear());
                    official.setApprovedByManagerid(manager);
                    official.setPublishedDate(Instant.now());
                    return official;
                }).collect(Collectors.toList());

        officialRepository.saveAll(officialSchedules);
        draftRepository.deleteByEmployeeIDInAndMonthYear(employeesInDept, monthYear);
    }


    private UserEntity getUserOrThrow(Integer employeeId) {
        return userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));
    }

    private ShiftEntity getShiftOrThrow(Integer shiftId) {
        return shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + shiftId));
    }

    // Lấy lịch chính thức của phòng ban để dễ theo dõi
    public List<EmployeeDraftSummaryDTO> getDepartmentOfficialSchedules(Integer managerId, String monthYear) {
        UserEntity manager = getUserOrThrow(managerId);
        if (manager.getDepartmentID() == null) {
            throw new RuntimeException("Manager không được gán vào phòng ban nào.");
        }
        DepartmentEntity departmentId = manager.getDepartmentID().getManagerID().getDepartmentID();
        List<UserEntity> employeesInDept = userRepository.findByDepartmentID(departmentId);
        List<EmployeeofficialscheduleEntity> officials = officialRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        Map<Integer, List<DraftRegistrationDTO>> schedulesByEmployeeId = officials.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getEmployeeID().getId(),
                        Collectors.mapping(this::mapOfficialEntityToDTO, Collectors.toList())
                ));

        return employeesInDept.stream()
                .map(employee -> new EmployeeDraftSummaryDTO(
                        employee.getId(),
                        employee.getFullname(),
                        schedulesByEmployeeId.getOrDefault(employee.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    //Sửa lịch chính thức
    @Transactional
    public void updateOfficialScheduleBatch(List<ManagerOfficialUpdateDTO> dtos, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        Integer departmentId = manager.getDepartmentID().getId();

        List<EmployeeofficialscheduleEntity> schedulesToSave = new ArrayList<>();

        for (ManagerOfficialUpdateDTO dto : dtos) {
            UserEntity employee = getUserOrThrow(dto.employeeId());

            if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
                throw new SecurityException("Manager không có quyền chỉnh sửa lịch của nhân viên: " + employee.getFullname());
            }

            Optional<EmployeeofficialscheduleEntity> existingOfficialOpt = officialRepository.findByEmployeeIDAndDate(employee, dto.date());
            boolean isDayOffOrBlank = (dto.isDayOff() || dto.shiftId() == null);

            if (isDayOffOrBlank) {
                EmployeeofficialscheduleEntity officialSchedule = existingOfficialOpt.orElse(new EmployeeofficialscheduleEntity());
                officialSchedule.setEmployeeID(employee);
                officialSchedule.setDate(dto.date());
                officialSchedule.setShiftID(null);
                officialSchedule.setIsDayOff(dto.isDayOff());
                officialSchedule.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                officialSchedule.setApprovedByManagerid(manager);
                officialSchedule.setPublishedDate(Instant.now());
                schedulesToSave.add(officialSchedule);

            } else {
                EmployeeofficialscheduleEntity officialSchedule = existingOfficialOpt.orElse(new EmployeeofficialscheduleEntity());
                officialSchedule.setEmployeeID(employee);
                officialSchedule.setDate(dto.date());
                officialSchedule.setShiftID(getShiftOrThrow(dto.shiftId()));
                officialSchedule.setIsDayOff(false);
                officialSchedule.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                officialSchedule.setApprovedByManagerid(manager);
                officialSchedule.setPublishedDate(Instant.now());
                schedulesToSave.add(officialSchedule);
            }
        }

        if (!schedulesToSave.isEmpty()) {
            officialRepository.saveAll(schedulesToSave);
        }
    }

    //Lịch xếp cho nhân viên mới
    @Transactional
    public void initializeScheduleForNewEmployee(Integer managerId, NewEmployeeScheduleDTO dto) {

        UserEntity manager = getUserOrThrow(managerId);
        Integer departmentId = manager.getDepartmentID().getId();


        UserEntity employee = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với username: " + dto.getUsername()));

        // Manager và Employee phải cùng phòng ban
        if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
            throw new SecurityException("Manager không có quyền xếp lịch cho nhân viên: " + employee.getFullname());
        }

        ShiftEntity selectedShift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.getShiftId()));

        // Xác định khoảng thời gian (Từ startDate đến hết tháng)
        java.time.LocalDate startDate = dto.getStartDate();
        java.time.YearMonth yearMonth = java.time.YearMonth.from(startDate);
        java.time.LocalDate endOfMonth = yearMonth.atEndOfMonth();
        String monthYearStr = startDate.format(MONTH_YEAR_FORMATTER);

        List<EmployeeofficialscheduleEntity> schedulesToSave = new ArrayList<>();

        for (java.time.LocalDate date = startDate; !date.isAfter(endOfMonth); date = date.plusDays(1)) {

            // Check trùng lặp: Nếu ngày đó đã có lịch rồi thì bỏ qua
            Optional<EmployeeofficialscheduleEntity> existing = officialRepository.findByEmployeeIDAndDate(employee, date);
            if (existing.isPresent()) continue;

            EmployeeofficialscheduleEntity official = new EmployeeofficialscheduleEntity();
            official.setEmployeeID(employee);
            official.setDate(date);
            official.setMonthYear(monthYearStr);
            official.setApprovedByManagerid(manager);
            official.setPublishedDate(Instant.now());

            //Cứ 7 ngày thì có 1 ngày Off
            long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, date);

            boolean isCycleDayOff = (daysDiff + 1) % 7 == 0;

            if (isCycleDayOff) {
                official.setShiftID(null);
                official.setIsDayOff(true);
            } else {
                official.setShiftID(selectedShift);
                official.setIsDayOff(false);
            }

            schedulesToSave.add(official);
        }

        if (!schedulesToSave.isEmpty()) {
            officialRepository.saveAll(schedulesToSave);
        }
    }
}