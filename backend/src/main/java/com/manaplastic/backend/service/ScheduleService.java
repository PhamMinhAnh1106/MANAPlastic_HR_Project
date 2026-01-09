package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.schedule.*;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
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

            Optional<EmployeedraftscheduleEntity> existingDraftOpt = draftRepository.findByEmployeeIDAndDate(employee, dto.getDate());
            boolean isActiveChoice = (dto.getShiftId() != null || dto.isDayOff());

            if (isActiveChoice) {
                EmployeedraftscheduleEntity draft = existingDraftOpt.orElse(new EmployeedraftscheduleEntity());
                draft.setEmployeeID(employee);
                draft.setDate(dto.getDate());

                if (dto.getShiftId() != null) {
                    ShiftEntity shift = shiftRepository.findById(dto.getShiftId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.getShiftId()));
                    draft.setShiftID(shift);
                } else {
                    draft.setShiftID(null);
                }

                draft.setIsDayOff(dto.isDayOff());
                draft.setMonthYear(dto.getDate().format(MONTH_YEAR_FORMATTER));
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
    @Transactional
    public void registerBatchSchedule(BatchScheduleRegistrationDTO batchDto, Integer employeeId) {

        Map<LocalDate, Integer> scheduleMap = new HashMap<>();

        if (batchDto.getFromDate() != null && batchDto.getToDate() != null && batchDto.getRangeShiftId() != null) {
            if (batchDto.getFromDate().isAfter(batchDto.getToDate())) {
                throw new RuntimeException("Ngày bắt đầu không được sau ngày kết thúc.");
            }

            LocalDate currentDate = batchDto.getFromDate();
            while (!currentDate.isAfter(batchDto.getToDate())) {
                scheduleMap.put(currentDate, batchDto.getRangeShiftId());
                currentDate = currentDate.plusDays(1);
            }
        }


        if (batchDto.getSpecificDays() != null && !batchDto.getSpecificDays().isEmpty()) {
            for (DraftRegistrationDTO specific : batchDto.getSpecificDays()) {
                if (specific.getDate() != null && specific.getShiftId() != null) {
                    scheduleMap.put(specific.getDate(), specific.getShiftId());
                }
            }
        }

        if (scheduleMap.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn khoảng thời gian hoặc danh sách ngày đăng ký.");
        }

        List<DraftRegistrationDTO> finalList = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : scheduleMap.entrySet()) {
            DraftRegistrationDTO dto = new DraftRegistrationDTO();
            dto.setDate(entry.getKey());
            dto.setShiftId(entry.getValue());
            finalList.add(dto);
        }

        registerDraftSchedule(finalList, employeeId);
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
        DraftRegistrationDTO dto = new DraftRegistrationDTO();

        dto.setDate(entity.getDate());
        dto.setDayOff(entity.getIsDayOff());

        if (entity.getShiftID() != null) {
            dto.setShiftId(entity.getShiftID().getId());
            dto.setShiftName(entity.getShiftID().getShiftname());
        }

        return dto;
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
        DraftRegistrationDTO dto = new DraftRegistrationDTO();

        dto.setDate(entity.getDate());
        dto.setDayOff(entity.getIsDayOff());

        if (entity.getShiftID() != null) {
            dto.setShiftId(entity.getShiftID().getId());
            dto.setShiftName(entity.getShiftID().getShiftname());
        }

        return dto;
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

        //  Manager và Employee phải cùng phòng ban
        if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
            throw new SecurityException("Manager không có quyền xếp lịch cho nhân viên: " + employee.getFullname());
        }

        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new RuntimeException("Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu không được sau ngày kết thúc.");
        }


        ShiftEntity mainShift = null;
        if (dto.getShiftId() != null) {
            mainShift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc chính với ID: " + dto.getShiftId()));
        }
        //map cho ngày ngoại lệ
        Map<LocalDate, DraftRegistrationDTO> exceptionMap = new HashMap<>();
        if (dto.getSpecificDays() != null) {
            for (DraftRegistrationDTO exception : dto.getSpecificDays()) {
                if (exception.getDate() != null) {
                    exceptionMap.put(exception.getDate(), exception);
                }
            }
        }

        List<EmployeeofficialscheduleEntity> schedulesToSave = new ArrayList<>();

        // Vòng lặp từ StartDate -> EndDate
        for (LocalDate date = dto.getStartDate(); !date.isAfter(dto.getEndDate()); date = date.plusDays(1)) {
            EmployeeofficialscheduleEntity official = new EmployeeofficialscheduleEntity();
            official.setEmployeeID(employee);
            official.setDate(date);

            official.setMonthYear(date.format(MONTH_YEAR_FORMATTER));
            official.setApprovedByManagerid(manager);
            official.setPublishedDate(Instant.now());

            // Trường hợp 1: Ngày này nằm trong danh sách ngoại lệ
            if (exceptionMap.containsKey(date)) {
                DraftRegistrationDTO exceptionDto = exceptionMap.get(date);

                if (exceptionDto.isDayOff()) {
                    official.setIsDayOff(true);
                    official.setShiftID(null);
                } else if (exceptionDto.getShiftId() != null) {
                    ShiftEntity specificShift = shiftRepository.findById(exceptionDto.getShiftId())
                            .orElseThrow(() -> new RuntimeException("Ca làm việc ngoại lệ không tồn tại ID: " + exceptionDto.getShiftId()));

                    official.setIsDayOff(false);
                    official.setShiftID(specificShift);
                } else {
                    // Ngoại lệ nhưng không set ca cũng ko set dayoff -> Fallback về ca chính
                    if (mainShift != null) {
                        official.setIsDayOff(false);
                        official.setShiftID(mainShift);
                    } else {
                        // Không có ca chính luôn -> set nghỉ
                        official.setIsDayOff(true);
                        official.setShiftID(null);
                    }
                }
            }
            // Trường hợp 2: Ngày thường (Dùng ca chính)
            else {
                if (mainShift != null) {
                    official.setIsDayOff(false);
                    official.setShiftID(mainShift);
                } else {
                    // Nếu không có ca chính và không phải ngoại lệ -> Mặc định là ngày nghỉ
                    official.setIsDayOff(true);
                    official.setShiftID(null);
                }
            }

            schedulesToSave.add(official);
        }

        if (!schedulesToSave.isEmpty()) {
            officialRepository.saveAll(schedulesToSave);
        }
    }
}