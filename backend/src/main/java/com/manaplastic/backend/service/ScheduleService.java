package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.*;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
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
    private final LeaveRequestRepository leaveRequestRepository;
    private final ScheduleRequirementRepository requirementRepository;

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

            // Tìm xem ngày này đã được lưu trước đó chưa
            Optional<EmployeedraftscheduleEntity> existingDraftOpt = draftRepository.findByEmployeeIDAndDate(employee, dto.date());
            boolean isActiveChoice = (dto.shiftId() != null || dto.isDayOff());

            if (isActiveChoice) {
                // Nhân viên có chọn ca hoặc chọn nghỉ
                EmployeedraftscheduleEntity draft = existingDraftOpt.orElse(new EmployeedraftscheduleEntity());
                draft.setEmployeeID(employee);
                draft.setDate(dto.date());

                if (dto.shiftId() != null) {
                    ShiftEntity shift = shiftRepository.findById(dto.shiftId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc với ID: " + dto.shiftId()));
                    draft.setShiftID(shift);
                } else {
                    draft.setShiftID(null); // Trường hợp chọn nghỉ
                }

                draft.setIsDayOff(dto.isDayOff());
                draft.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                draft.setRegistrationDate(Instant.now());

                schedulesToSave.add(draft);

            } else {
                // Nhân viên để trống
                if (existingDraftOpt.isPresent()) {
                    // Nếu ngày này đã từng được lưu nhưng bây giờ nhân viên muốn xóa đi (để trống) thì thêm vào danh sách chờ xóa.
                    schedulesToDelete.add(existingDraftOpt.get());
                }
                // Nếu ngày này vốn đã trống (không có trong DB) thì không làm gì cả (Ignore)
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
        String shiftName= null;
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

   //y chang map to của lịch nháp
    private DraftRegistrationDTO mapOfficialEntityToDTO(EmployeeofficialscheduleEntity entity) {
        Integer shiftId = null;
        String shiftName= null;
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
            throw new RuntimeException("Manager không được gán vào phòng ban nào."); // quản lý phòng ban nào lấy đúng nhân viên của phòng đó thôi
        }
        DepartmentEntity departmentId = manager.getDepartmentID().getManagerID().getDepartmentID();
        List<UserEntity> employeesInDept = userRepository.findByDepartmentID(departmentId);
        List<EmployeedraftscheduleEntity> drafts = draftRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        // nhóm các bản nháp theo ID nhân viên (để tối ưu)
        Map<Integer, List<DraftRegistrationDTO>> draftsByEmployeeId = drafts.stream()
                .collect(Collectors.groupingBy(
                        draft -> draft.getEmployeeID().getId(), // Nhóm theo Employee ID
                        Collectors.mapping(this::mapEntityToDTO, Collectors.toList()) // Chuyển đổi sang DTO
                ));

        // Tạo DTO trả về (bao gồm cả nhân viên không đăng ký gì)
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

            // KIỂM TRA BẢO MẬT: Manager có quyền sửa nhân viên này không?
            if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
                throw new SecurityException("Manager không có quyền chỉnh sửa lịch của nhân viên: " + employee.getFullname());
            }

            Optional<EmployeedraftscheduleEntity> existingDraftOpt = draftRepository.findByEmployeeIDAndDate(employee, dto.date());
            boolean isActiveChoice = (dto.shiftId() != null || dto.isDayOff());

            if (isActiveChoice) {
                // LƯU/CẬP NHẬT
                EmployeedraftscheduleEntity draft = existingDraftOpt.orElse(new EmployeedraftscheduleEntity());
                draft.setEmployeeID(employee);
                draft.setDate(dto.date());
                draft.setShiftID(dto.shiftId() != null ? getShiftOrThrow(dto.shiftId()) : null);
                draft.setIsDayOff(dto.isDayOff());
                draft.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                draft.setRegistrationDate(Instant.now());
                schedulesToSave.add(draft);
            } else {
                // XÓA
                existingDraftOpt.ifPresent(schedulesToDelete::add);
            }
        }

        if (!schedulesToSave.isEmpty()) draftRepository.saveAll(schedulesToSave);
        if (!schedulesToDelete.isEmpty()) draftRepository.deleteAll(schedulesToDelete);
    }

//
//     "HOÀN TẤT" Lịch
//     Chuyển từ Nháp sang Chính thức và Xóa Nháp

    @Transactional
    public void finalizeSchedule(String monthYear, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        DepartmentEntity departmentId = manager.getDepartmentID().getManagerID().getDepartmentID();
        List<UserEntity> employeesInDept = userRepository.findByDepartmentID(departmentId);

        // Lấy tất cả bản nháp cuối cùng của phòng ban
        List<EmployeedraftscheduleEntity> draftsToFinalize = draftRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        // xóa lịch CHÍNH THỨC CŨ (nếu có) của tháng này
        officialRepository.deleteByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

       // Chuyển đổi Nháp -> Chính thức
        List<EmployeeofficialscheduleEntity> officialSchedules = draftsToFinalize.stream()
                .map(draft -> {
                    EmployeeofficialscheduleEntity official = new EmployeeofficialscheduleEntity();
                    official.setEmployeeID(draft.getEmployeeID());
                    official.setDate(draft.getDate());
                    official.setShiftID(draft.getShiftID());
                    official.setIsDayOff(draft.getIsDayOff());
                    official.setMonthYear(draft.getMonthYear());
                    official.setApprovedByManagerid(manager); // Gán Manager đã duyệt
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

        // Lấy tất cả lịch CHÍNH THỨC của nhóm nhân viên này
        List<EmployeeofficialscheduleEntity> officials = officialRepository.findByEmployeeIDInAndMonthYear(employeesInDept, monthYear);

        // Nhóm các bản lịch theo ID nhân viên
        Map<Integer, List<DraftRegistrationDTO>> schedulesByEmployeeId = officials.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getEmployeeID().getId(), // Nhóm theo Employee ID
                        Collectors.mapping(this::mapOfficialEntityToDTO, Collectors.toList())
                ));

        // DTO trả về (y chang cách lấy lịch nháp)
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
        List<EmployeeofficialscheduleEntity> schedulesToDelete = new ArrayList<>(); // Cho trường hợp ốm

        for (ManagerOfficialUpdateDTO dto : dtos) {
            UserEntity employee = getUserOrThrow(dto.employeeId());

            // KIỂM TRA BẢO MẬT: Manager có quyền sửa nhân viên này không?
            if (employee.getDepartmentID() == null || !employee.getDepartmentID().getId().equals(departmentId)) {
                throw new SecurityException("Manager không có quyền chỉnh sửa lịch của nhân viên: " + employee.getFullname());
            }

            // Tìm bản ghi CHÍNH THỨC hiện có
            Optional<EmployeeofficialscheduleEntity> existingOfficialOpt = officialRepository.findByEmployeeIDAndDate(employee, dto.date());


            // Nếu Manager chọn "Nghỉ" (ốm) hoặc "Trống"
            boolean isDayOffOrBlank = (dto.isDayOff() || dto.shiftId() == null);

            if (isDayOffOrBlank) {
                // Xử lý nghỉ ốm (isDayOff = true)
                // Hoặc xóa ca (nếu isDayOff = false và shiftId = null)

                EmployeeofficialscheduleEntity officialSchedule = existingOfficialOpt.orElse(new EmployeeofficialscheduleEntity());

                officialSchedule.setEmployeeID(employee);
                officialSchedule.setDate(dto.date());
                officialSchedule.setShiftID(null); // Bị ốm nên không có ca
                officialSchedule.setIsDayOff(dto.isDayOff()); // Sẽ là TRUE nếu ốm
                officialSchedule.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                officialSchedule.setApprovedByManagerid(manager);
                officialSchedule.setPublishedDate(Instant.now());

                schedulesToSave.add(officialSchedule);

            } else {
                // Xử lý đổi ca (ví dụ: đi trễ C808 -> C809)
                EmployeeofficialscheduleEntity officialSchedule = existingOfficialOpt.orElse(new EmployeeofficialscheduleEntity());

                officialSchedule.setEmployeeID(employee);
                officialSchedule.setDate(dto.date());
                officialSchedule.setShiftID(getShiftOrThrow(dto.shiftId())); // Đổi ca mới
                officialSchedule.setIsDayOff(false); // Đi làm, không nghỉ
                officialSchedule.setMonthYear(dto.date().format(MONTH_YEAR_FORMATTER));
                officialSchedule.setApprovedByManagerid(manager);
                officialSchedule.setPublishedDate(Instant.now());

                schedulesToSave.add(officialSchedule);
            }
        }

        // Lưu các thay đổi vào bảng CHÍNH THỨC
        if (!schedulesToSave.isEmpty()) {
            officialRepository.saveAll(schedulesToSave);
        }
    }

    // Auto xếp ca
    @Transactional
    public void autoAssignBlankSchedules(String monthYear, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        if (manager.getDepartmentID() == null) {
            throw new RuntimeException("Manager không có phòng ban.");
        }
        DepartmentEntity department = manager.getDepartmentID();

        List<UserEntity> employees = userRepository.findByDepartmentID(department.getManagerID().getDepartmentID());

        // Lấy các quy tắc (tiêu chí) của phòng ban
        List<SchedulerequirementEntity> rules = requirementRepository.findByDepartmentID(department);

        // Lấy danh sách ngày trong tháng
        List<LocalDate> daysInMonth = getDatesForMonth(monthYear);
        LocalDate monthStart = daysInMonth.get(0);
        LocalDate monthEnd = daysInMonth.get(daysInMonth.size() - 1);

        // Lấy Bể Không Có Mặt (Nghỉ phép, ...)
        Set<String> unavailablePool = getUnavailableEmployeeDays(employees, monthStart, monthEnd);

        // Lấy Bể Đã Đăng Ký (Nhân viên tự đăng ký) -> dùng map để check
        Map<String, EmployeedraftscheduleEntity> manualDraftMap =
                draftRepository.findByEmployeeIDInAndMonthYear(employees, monthYear)
                        .stream()
                        .collect(Collectors.toMap(
                                draft -> draft.getEmployeeID().getId() + ":" + draft.getDate(),
                                draft -> draft
                        ));

        // --- THUẬT TOÁN XẾP CA ---
        List<EmployeedraftscheduleEntity> newAssignments = new ArrayList<>();

        for (LocalDate day : daysInMonth) {

            // Xây dựng Bể Sẵn Sàng (Available Pool) cho ngày hôm nay
            // Bao gồm những người không nghỉ và chưa được gán ca (thủ công)
            List<UserEntity> availableEmployees = new ArrayList<>();
            for (UserEntity emp : employees) {
                String unavailableKey = emp.getId() + ":" + day;
                String draftKey = emp.getId() + ":" + day;

                if (!unavailablePool.contains(unavailableKey) && !manualDraftMap.containsKey(draftKey)) {
                    availableEmployees.add(emp);
                }
            }

            // Lặp qua từng Quy tắc (từng ca cần xếp)
            for (SchedulerequirementEntity rule : rules) {

                int totalNeeded = rule.getTotalStaffNeeded();
                List<RequirementrulesEntity> skillRules = rule.getRules(); // Lấy các rule con

                int assignedCount = 0;

                // (Logic Ưu tiên 1: Gán người có Skill cao trước)
                for (RequirementrulesEntity skillRule : skillRules) {
                    int skillGradeNeeded = skillRule.getRequiredSkillgrade();
                    int minStaffNeeded = skillRule.getMinStaffCount();

                    // Tìm người
                    List<UserEntity> foundEmployees = findEmployeesBySkill(
                            availableEmployees, skillGradeNeeded, minStaffNeeded
                    );

                    // Gán và thêm vào DB
                    for(UserEntity foundEmp : foundEmployees) {
                        newAssignments.add(createDraftEntity(foundEmp, day, rule.getShiftID(), monthYear));
                        availableEmployees.remove(foundEmp);
                        assignedCount++;
                    }
                }

                // (Logic Ưu tiên 2: Gán người còn lại (skill bất kỳ) cho đủ)
                int remainingNeeded = totalNeeded - assignedCount;
                if (remainingNeeded > 0 && !availableEmployees.isEmpty()) {
                    List<UserEntity> foundOthers = findEmployeesBySkill(
                            availableEmployees, 0, remainingNeeded // Skill 0 = bất kỳ
                    );

                    for(UserEntity foundEmp : foundOthers) {
                        newAssignments.add(createDraftEntity(foundEmp, day, rule.getShiftID(), monthYear));
                        availableEmployees.remove(foundEmp);
                    }
                }
                // Nếu không tìm đủ thì bỏ qua và thông báo sau
            }
        }

        if (!newAssignments.isEmpty()) {
            draftRepository.saveAll(newAssignments);
        }

    }

    // Helper: Lấy Bể Không Có Mặt (Nghỉ phép + Tự đăng ký nghỉ)
    private Set<String> getUnavailableEmployeeDays(List<UserEntity> employees, LocalDate monthStart, LocalDate monthEnd) {
        Set<String> unavailablePool = new HashSet<>();

        // Nguồn 1: Lấy từ Đơn Nghỉ Phép (leaverequests) đã duyệt
        List<LeaverequestEntity> leaves = leaveRequestRepository.findApprovedLeavesIntersectingMonth(
                employees, monthStart, monthEnd
        );

        for (LeaverequestEntity leave : leaves) {
            // Lấy ngày bắt đầu và kết thúc của đơn nghỉ
            LocalDate leaveStart = leave.getStartdate();
            LocalDate leaveEnd = leave.getEnddate();

            // Chỉ lặp trong phạm vi tháng đang xét (để tối ưu)
            LocalDate dayIterator = leaveStart.isBefore(monthStart) ? monthStart : leaveStart;
            LocalDate loopEnd = leaveEnd.isAfter(monthEnd) ? monthEnd : leaveEnd;

            // Thêm tất cả các ngày (trong phạm vi) vào Set
            for (LocalDate date = dayIterator; !date.isAfter(loopEnd); date = date.plusDays(1)) {
                String key = leave.getUserID().getId() + ":" + date;
                unavailablePool.add(key);
            }
        }

        // Nguồn 2: Lấy từ Bảng Nháp (employeedraftschedule)
        // Nơi nhân viên tự đăng ký
        String monthYear = monthStart.format(MONTH_YEAR_FORMATTER);
        List<EmployeedraftscheduleEntity> manualDayOffs = draftRepository.findByEmployeeIDInAndMonthYearAndIsDayOff(
                employees, monthYear, true
        );

        for (EmployeedraftscheduleEntity draft : manualDayOffs) {
            String key = draft.getEmployeeID().getId() + ":" + draft.getDate();
            unavailablePool.add(key);
        }

        return unavailablePool;
    }

    // Helper: Lấy danh sách các ngày (LocalDate) trong một tháng (YYYY-MM).

    private List<LocalDate> getDatesForMonth(String monthYearStr) {
        YearMonth yearMonth = YearMonth.parse(monthYearStr, MONTH_YEAR_FORMATTER);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());
    }

    // Helper: Tạo một đối tượng Entity nháp mới

    private EmployeedraftscheduleEntity createDraftEntity(UserEntity emp, LocalDate day, ShiftEntity shift, String monthYear) {
        EmployeedraftscheduleEntity newDraft = new EmployeedraftscheduleEntity();
        newDraft.setEmployeeID(emp);
        newDraft.setDate(day);
        newDraft.setShiftID(shift);
        newDraft.setIsDayOff(false);
        newDraft.setMonthYear(monthYear);
        newDraft.setRegistrationDate(Instant.now());
        return newDraft;
    }

    // Helper: Tìm nhân viên theo skill

    private List<UserEntity> findEmployeesBySkill(List<UserEntity> availableEmployees, int minSkill, int count) {
        List<UserEntity> found = availableEmployees.stream()
                .filter(emp -> emp.getSkillGrade() >= minSkill)
                .limit(count)
                .collect(Collectors.toList());
        return found;
    }

    @Transactional(readOnly = true)
    public List<ScheduleValidationDTO> validateDraftSchedule(String monthYear, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        DepartmentEntity department = manager.getDepartmentID();

        List<SchedulerequirementEntity> rules = requirementRepository.findByDepartmentID(department);
        List<LocalDate> daysInMonth = getDatesForMonth(monthYear);
        List<UserEntity> employees = userRepository.findByDepartmentID(department.getManagerID().getDepartmentID());
        List<EmployeedraftscheduleEntity> allDrafts = draftRepository.findByEmployeeIDInAndMonthYear(employees, monthYear);

        Map<String, List<UserEntity>> draftMap = allDrafts.stream()
                .filter(draft -> draft.getShiftID() != null) // Chỉ quan tâm các ca có gán
                .collect(Collectors.groupingBy(
                        draft -> draft.getDate() + ":" + draft.getShiftID().getId(),
                        Collectors.mapping(EmployeedraftscheduleEntity::getEmployeeID, Collectors.toList())
                ));

        // LẶP VÀ KIỂM TRA
        List<ScheduleValidationDTO> validationResults = new ArrayList<>();

        for (LocalDate day : daysInMonth) {
            for (SchedulerequirementEntity rule : rules) { // Lặp qua C808, C813...

                // Gọi hàm helper để kiểm tra từng quy tắc
                validationResults.add(
                        validateSingleRule(day, rule, draftMap)
                );
            }
        }
        return validationResults;
    }

    // Hàm Helper: Kiểm tra một quy tắc cụ thể (ví dụ: C808) trong một ngày cụ thể.

    private ScheduleValidationDTO validateSingleRule(LocalDate day,
                                                     SchedulerequirementEntity rule,
                                                     Map<String, List<UserEntity>> draftMap) {

        Integer shiftId = rule.getShiftID().getId();
        String shiftName = rule.getShiftID().getShiftname(); // Giả sử hàm getter là getShiftname()
        String lookupKey = day + ":" + shiftId;

        List<UserEntity> assignedEmployees = draftMap.getOrDefault(lookupKey, List.of());
        int totalAssigned = assignedEmployees.size();

        // Bắt đầu kiểm tra
        List<String> violations = new ArrayList<>();
        boolean isCompliant = true;

        // Kiểm tra TỔNG SỐ LƯỢNG (Ví dụ: Cần 3, nhưng chỉ có 2)
        int totalNeeded = rule.getTotalStaffNeeded();
        if (totalAssigned < totalNeeded) {
            isCompliant = false;
            violations.add(String.format("Thiếu %d nhân viên (%d/%d)",
                    (totalNeeded - totalAssigned), totalAssigned, totalNeeded));
        }

        // Kiểm tra QUY TẮC KỸ NĂNG (Ví dụ: Cần 1 Lvl 3, nhưng chỉ có 0)
        for (RequirementrulesEntity skillRule : rule.getRules()) {
            int gradeNeeded = skillRule.getRequiredSkillgrade();
            int minStaffNeeded = skillRule.getMinStaffCount();

            // Đếm xem có bao nhiêu người gán có skill >= yêu cầu
            long staffWithSkill = assignedEmployees.stream()
                    .filter(emp -> emp.getSkillGrade() >= gradeNeeded)
                    .count();

            if (staffWithSkill < minStaffNeeded) {
                isCompliant = false;
                violations.add(String.format("Thiếu %d Lvl %d (%d/%d)",
                        (minStaffNeeded - staffWithSkill), gradeNeeded, staffWithSkill, minStaffNeeded));
            }
        }

        // Tạo thông báo
        String message = isCompliant ? "Tuân thủ" : String.join(". ", violations);

        return new ScheduleValidationDTO(
                day,
                shiftId,
                shiftName,
                isCompliant,
                message
        );
    }


}
