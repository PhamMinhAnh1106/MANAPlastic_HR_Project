package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.DraftRegistrationDTO;
import com.manaplastic.backend.DTO.EmployeeDraftSummaryDTO;
import com.manaplastic.backend.DTO.ManagerDraftUpdateDTO;
import com.manaplastic.backend.DTO.ManagerOfficialUpdateDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.repository.EmployeeDraftScheduleRepository;
import com.manaplastic.backend.repository.EmployeeOfficialScheduleRepository;
import com.manaplastic.backend.repository.ShiftRepository;
import com.manaplastic.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if (entity.getShiftID() != null) {
            shiftId = entity.getShiftID().getId();
        }

        return new DraftRegistrationDTO(
                entity.getDate(),
                shiftId,
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
        if (entity.getShiftID() != null) {
            shiftId = entity.getShiftID().getId();
        }

        return new DraftRegistrationDTO(
                entity.getDate(),
                shiftId,
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
}
