package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.ScheduleValidationDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoAssignScheduleService {

    private final EmployeeDraftScheduleRepository draftRepository;
    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ScheduleRequirementRepository requirementRepository;

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    /**
     *================================= Thuật toán xếp ca tự động ======================================
     * * Logic:
     * 1. Lặp qua TỪNG NGÀY trong tháng (theo thứ tự).
     * 2. Với mỗi ngày, TẤT CẢ nhân viên được chia làm 3 nhóm:
         * a. Đã đăng ký nghỉ (Nghỉ phép, tự off): Cập nhật trạng thái (reset ngày làm liên tiếp).
         * b. Đã đăng ký làm (Tự chọn ca): Cập nhật trạng thái (tăng ngày làm, lưu ca).
         * c. Để trống (Chờ xếp ca): sau khi đã lọc 2 trh trên vào pool unavailabe emloyee thì sẽ chạy xếp ca tự động.
     * 3. Lặp qua TỪNG QUY TẮC (rule) cho ngày hôm đó (ví dụ: C808 cần 3 người).
     * 4. Lọc nhóm (c) để tìm các ứng viên "có thể làm" (hợp lệ) - pool available employee
         * - Không vi phạm 6 ngày làm liên tiếp.
         * - Không vi phạm quy tắc chuyển ca (Nghỉ 1 ngày nếu chuyển từ Tối -> Sáng - Luật lao động cách mỗi ca ít nhâất 6-8 tiếng).
     * 5. SẮP XẾP các ứng viên hợp lệ:
         * - Ưu tiên người có TỔNG NGÀY LÀM ít nhất.
         * - Ưu tiên người có SỐ CA CÙNG LOẠI ít nhất (để cân bằng ca Sáng/Tối).
     * 6. Chọn người tốt nhất từ danh sách đã sắp xếp, gán ca, và cập nhật trạng thái của họ.
     * 7. Những người ở nhóm (c) không được gán ca nào -> tự động được nghỉ (cập nhật trạng thái, shiftID = null và isdayoff = true).
     * ===================================================================================================
     * CHÚ Ý: các trường hợp ngày phép - nghỉ vẫn có lương như AL,SL,PL,ML thì vẫn sẽ có shiftID tương ứng nên thuật toán vẫn chạy bình thường và đưa các ca này
     * vào pool (bể) unvailabeEmployee (nhân viên không sẵn có)
     */

//     Lớp nội bộ dùng để theo dõi trạng thái xếp lịch của một nhân viên
//    khi thuật toán chạy qua từng ngày.

    private static class EmployeeScheduleState {
        int consecutiveWorkDays = 0;
        LocalDate lastWorkDay = null;
        ShiftEntity lastAssignedShift = null;
        // Key: shiftId - Value: count
        Map<Integer, Integer> shiftCounts = new HashMap<>();

        public int getTotalWorkDays() {
            return shiftCounts.values().stream().mapToInt(Integer::intValue).sum();
        }

        public int getShiftCount(Integer shiftId) {
            return shiftCounts.getOrDefault(shiftId, 0);
        }
    }


    @Transactional
    public void autoAssignBlankSchedules(String monthYear, Integer managerId) {
        UserEntity manager = getUserOrThrow(managerId);
        DepartmentEntity department = manager.getDepartmentID();

        List<UserEntity> allEmployees = userRepository.findByDepartmentID(department.getManagerID().getDepartmentID());
        List<SchedulerequirementEntity> rules = requirementRepository.findByDepartmentID(department);
        List<LocalDate> daysInMonth = getDatesForMonth(monthYear);
        LocalDate monthStart = daysInMonth.get(0);
        LocalDate monthEnd = daysInMonth.get(daysInMonth.size() - 1);

        // Lấy Nhân viên đã đăng ký trước cho vaof "Bể Dữ Liệu" - unvailu
        Set<String> unavailablePool = getUnavailableEmployeeDays(allEmployees, monthStart, monthEnd);
        Map<String, EmployeedraftscheduleEntity> manualDraftMap =
                draftRepository.findByEmployeeIDInAndMonthYear(allEmployees, monthYear)
                        .stream()
                        .collect(Collectors.toMap(
                                draft -> draft.getEmployeeID().getId() + ":" + draft.getDate(),
                                draft -> draft
                        ));

        // Khởi tạo Trạng thái (State) cho tất cả nhân viên
        Map<Integer, EmployeeScheduleState> employeeStates = new HashMap<>();
        for (UserEntity emp : allEmployees) {
            employeeStates.put(emp.getId(), new EmployeeScheduleState());
        }

        List<EmployeedraftscheduleEntity> newAssignments = new ArrayList<>();

        // Bắt đầu lặp qua TỪNG NGÀY
        for (LocalDate day : daysInMonth) {

            // Danh sách nhân viên cần được xếp ca trong ngày hôm nay
            List<UserEntity> employeesToProcessToday = new ArrayList<>();

            // Giai đoạn 1: Cập nhật trạng thái cho các nhân viên ĐÃ CÓ LỊCH
            for (UserEntity emp : allEmployees) {
                EmployeeScheduleState state = employeeStates.get(emp.getId());
                String unavailableKey = emp.getId() + ":" + day;
                String draftKey = emp.getId() + ":" + day;

                if (unavailablePool.contains(unavailableKey) || (manualDraftMap.containsKey(draftKey) && manualDraftMap.get(draftKey).getIsDayOff())) {
                    // Nhóm (a): Nhân viên này nghỉ (tự đăng ký hoặc nghỉ phép)
                    updateEmployeeStateForDayOff(state, day);
                } else if (manualDraftMap.containsKey(draftKey)) {
                    // Nhóm (b): Nhân viên này làm (tự đăng ký)
                    ShiftEntity manualShift = manualDraftMap.get(draftKey).getShiftID();
                    updateEmployeeStateForWorkDay(state, manualShift, day);
                } else {
                    // Nhóm (c): Nhân viên này để trống -> Chờ xếp ca
                    employeesToProcessToday.add(emp);
                }
            }

            // Giai đoạn 2: Xếp ca cho Nhóm (c)
            for (SchedulerequirementEntity rule : rules) {
                ShiftEntity shiftToAssign = rule.getShiftID();
                int totalNeeded = rule.getTotalStaffNeeded();
                int totalAssigned = 0;

                // Xếp ca theo skill (ưu tiên)
                for (RequirementrulesEntity skillRule : rule.getRules()) {
                    int skillGradeNeeded = skillRule.getRequiredSkillgrade();
                    int minStaffNeeded = skillRule.getMinStaffCount();

                    List<UserEntity> candidates = findAndSortCandidates(
                            employeesToProcessToday,
                            employeeStates,
                            shiftToAssign,
                            skillGradeNeeded
                    );

                    List<UserEntity> assigned = candidates.stream()
                            .limit(minStaffNeeded)
                            .collect(Collectors.toList());

                    // Gán ca và cập nhật
                    for (UserEntity empToAssign : assigned) {
                        newAssignments.add(createDraftEntity(empToAssign, day, shiftToAssign, monthYear));
                        employeesToProcessToday.remove(empToAssign); // Xóa khỏi danh sách chờ
                        updateEmployeeStateForWorkDay(employeeStates.get(empToAssign.getId()), shiftToAssign, day);
                        totalAssigned++;
                    }
                }

                // 2b. Xếp ca cho đủ (không cần skill)
                int remainingNeeded = totalNeeded - totalAssigned;
                if (remainingNeeded > 0) {
                    List<UserEntity> candidates = findAndSortCandidates(
                            employeesToProcessToday,
                            employeeStates,
                            shiftToAssign,
                            0 // Skill 0 = Bất kỳ
                    );

                    List<UserEntity> assigned = candidates.stream()
                            .limit(remainingNeeded)
                            .collect(Collectors.toList());

                    for (UserEntity empToAssign : assigned) {
                        newAssignments.add(createDraftEntity(empToAssign, day, shiftToAssign, monthYear));
                        employeesToProcessToday.remove(empToAssign);
                        updateEmployeeStateForWorkDay(employeeStates.get(empToAssign.getId()), shiftToAssign, day);
                    }
                }
            }

            // Giai đoạn 3: Ai còn sót lại trong danh sách chờ -> được nghỉ ( isdayoff = true)
            for (UserEntity unassignedEmp : employeesToProcessToday) {
                updateEmployeeStateForDayOff(employeeStates.get(unassignedEmp.getId()), day);
                newAssignments.add(createDayOffEntity(unassignedEmp, day, monthYear));
            }
        }

        if (!newAssignments.isEmpty()) {
            draftRepository.saveAll(newAssignments);
        }
    }

    // Helper: Lọc và Sắp xếp các ứng viên.
    private List<UserEntity> findAndSortCandidates(List<UserEntity> availableEmployees,
                                                   Map<Integer, EmployeeScheduleState> states,
                                                   ShiftEntity shiftToAssign,
                                                   int minSkillGrade) {
        return availableEmployees.stream()
                .filter(emp -> emp.getSkillGrade() >= minSkillGrade)
                .filter(emp -> canWorkShift(states.get(emp.getId()), shiftToAssign))
                .sorted(
                        // Ưu tiên người có TỔNG NGÀY LÀM ít nhất
                        Comparator.comparingInt((UserEntity emp) -> states.get(emp.getId()).getTotalWorkDays())
                                // Nếu bằng nhau, ưu tiên người có SỐ CA LOẠI NÀY ít nhất
                                .thenComparingInt((UserEntity emp) -> states.get(emp.getId()).getShiftCount(shiftToAssign.getId()))
                )
                .collect(Collectors.toList());
    }

    // Helper: Kiểm tra các quy tắc bắt buộc.
    private boolean canWorkShift(EmployeeScheduleState state, ShiftEntity shiftToAssign) {
        // QUY TẮC 1: Ngày nghỉ hàng tuần (Không làm quá 6 ngày liên tiếp)
        if (state.consecutiveWorkDays >= 6) {
            return false;
        }

        // QUY TẮC 2: Nghỉ chuyển ca (Tối -> Sáng)
        ShiftEntity lastShift = state.lastAssignedShift;
        if (lastShift != null && isNightShift(lastShift) && isMorningShift(shiftToAssign)) {
            return false;
        }

        // QUY TẮC 3: Cân bằng ca (không làm quá 15 ca cùng loại/tháng - xếp tự động xong ko ưng thì QL xếp tay lại sau)
        int maxShiftsPerType = 15;
        if (state.getShiftCount(shiftToAssign.getId()) >= maxShiftsPerType) {
            return false;
        }

        return true;
    }

    // Helper: Cập nhật trạng thái khi nhân viên LÀM VIỆC.
    private void updateEmployeeStateForWorkDay(EmployeeScheduleState state, ShiftEntity shift, LocalDate day) {
        // Kiểm tra ngày làm liên tiếp
        if (state.lastWorkDay != null && state.lastWorkDay.equals(day.minusDays(1))) {
            state.consecutiveWorkDays++;
        } else {
            state.consecutiveWorkDays = 1;
        }

        state.lastWorkDay = day;
        state.lastAssignedShift = shift;
        state.shiftCounts.put(shift.getId(), state.getShiftCount(shift.getId()) + 1);
    }

    // Helper: Cập nhật trạng thái khi nhân viên NGHỈ.
    private void updateEmployeeStateForDayOff(EmployeeScheduleState state, LocalDate day) {
        state.consecutiveWorkDays = 0;
        state.lastAssignedShift = null;
    }


    private boolean isNightShift(ShiftEntity shift) {
        return shift.getShiftname().equals("C813");
    }

    private boolean isMorningShift(ShiftEntity shift) {
        return shift.getShiftname().equals("C808");
    }


    // Helper: Lấy Bể Không Có Mặt (Nghỉ phép + Tự đăng ký nghỉ)
    private Set<String> getUnavailableEmployeeDays(List<UserEntity> employees, LocalDate monthStart, LocalDate monthEnd) {
        Set<String> unavailablePool = new HashSet<>();

        List<LeaverequestEntity> leaves = leaveRequestRepository.findApprovedLeavesIntersectingMonth(
                employees, monthStart, monthEnd
        );

        for (LeaverequestEntity leave : leaves) {
            LocalDate leaveStart = leave.getStartdate();
            LocalDate leaveEnd = leave.getEnddate();
            LocalDate dayIterator = leaveStart.isBefore(monthStart) ? monthStart : leaveStart;
            LocalDate loopEnd = leaveEnd.isAfter(monthEnd) ? monthEnd : leaveEnd;

            for (LocalDate date = dayIterator; !date.isAfter(loopEnd); date = date.plusDays(1)) {
                String key = leave.getUserID().getId() + ":" + date;
                unavailablePool.add(key);
            }
        }

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

    // Helper: Tạo một đối tượng Entity nháp MỚI cho ngày nghỉ
    private EmployeedraftscheduleEntity createDayOffEntity(UserEntity emp, LocalDate day, String monthYear) {
        EmployeedraftscheduleEntity newDraft = new EmployeedraftscheduleEntity();
        newDraft.setEmployeeID(emp);
        newDraft.setDate(day);
        newDraft.setShiftID(null);
        newDraft.setIsDayOff(true);
        newDraft.setMonthYear(monthYear);
        newDraft.setRegistrationDate(Instant.now());
        return newDraft;
    }

    // Helper: Lấy User
    private UserEntity getUserOrThrow(Integer employeeId) {
        return userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));
    }

    // ===============================================================================
    // ============================  CÁC HÀM VALIDATE  ===============================
    // ================================================================================

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

        List<ScheduleValidationDTO> validationResults = new ArrayList<>();

        for (LocalDate day : daysInMonth) {
            for (SchedulerequirementEntity rule : rules) {
                validationResults.add(
                        validateSingleRule(day, rule, draftMap)
                );
            }
        }
        return validationResults;
    }

    private ScheduleValidationDTO validateSingleRule(LocalDate day,
                                                     SchedulerequirementEntity rule,
                                                     Map<String, List<UserEntity>> draftMap) {

        Integer shiftId = rule.getShiftID().getId();
        String shiftName = rule.getShiftID().getShiftname();
        String lookupKey = day + ":" + shiftId;

        List<UserEntity> assignedEmployees = draftMap.getOrDefault(lookupKey, List.of());
        int totalAssigned = assignedEmployees.size();

        List<String> violations = new ArrayList<>();
        boolean isCompliant = true;

        int totalNeeded = rule.getTotalStaffNeeded();
        if (totalAssigned < totalNeeded) {
            isCompliant = false;
            violations.add(String.format("Thiếu %d nhân viên (%d/%d)",
                    (totalNeeded - totalAssigned), totalAssigned, totalNeeded));
        }

        for (RequirementrulesEntity skillRule : rule.getRules()) {
            int gradeNeeded = skillRule.getRequiredSkillgrade();
            int minStaffNeeded = skillRule.getMinStaffCount();

            long staffWithSkill = assignedEmployees.stream()
                    .filter(emp -> emp.getSkillGrade() >= gradeNeeded)
                    .count();

            if (staffWithSkill < minStaffNeeded) {
                isCompliant = false;
                violations.add(String.format("Thiếu %d Lvl %d (%d/%d)",
                        (minStaffNeeded - staffWithSkill), gradeNeeded, staffWithSkill, minStaffNeeded));
            }
        }

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