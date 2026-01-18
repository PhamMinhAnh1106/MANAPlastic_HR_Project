package com.manaplastic.backend.payrollengine.service;

import com.manaplastic.backend.DTO.criteria.PayrollFilterCriteria;
import com.manaplastic.backend.DTO.payroll.PayrollComponentDTO;
import com.manaplastic.backend.DTO.payroll.PayrollDTO;
import com.manaplastic.backend.DTO.payroll.PayrollDetailDTO;
import com.manaplastic.backend.entity.*;
import com.manaplastic.backend.filters.PayrollFilter;
import com.manaplastic.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class PayslipService {

    @Autowired private PayrollsRepository payrollRepository;
    @Autowired private ContractRepository contractRepo;
    @Autowired private ContractAllowancesRepository contractAllowanceRepo;
    @Autowired private OvertimeRequestRepository otRequestRepo;
    @Autowired private RewardPunishmentRepository rewardRepo;
    @Autowired private UserRepository userRepository; // Cần dùng khi HR xem lương theo ID

    public PayrollDetailDTO getPayrollDetail(UserEntity user, int month, int year) {
        String payPeriod = String.format("%d-%02d", year, month);
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        // 1. LẤY SNAPSHOT TỪ BẢNG PAYROLLS
        // Lưu ý: Repository cần hỗ trợ findByUserIDAndPayperiod(UserEntity user, String payPeriod)
        PayrollEntity payroll = payrollRepository.findByUserIDAndPayperiod(user, payPeriod)
                .orElseThrow(() -> new RuntimeException("Chưa có dữ liệu bảng lương cho kỳ " + payPeriod));

        BigDecimal actualWorkDays = payroll.getActualworkdays();
        BigDecimal baseSalary = payroll.getBasesalarysnapshot();

        // Mặc định chia 26
        BigDecimal standardDays = BigDecimal.valueOf(26);

        // Tính lương 1 giờ = (Lương cơ bản / 26) / 8
        BigDecimal hourlyRate = BigDecimal.ZERO;
        if (standardDays.compareTo(BigDecimal.ZERO) > 0) {
            hourlyRate = baseSalary.divide(standardDays, 4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(8), 4, RoundingMode.HALF_UP);
        }

        // TÍNH PHỤ CẤP (ALLOWANCES) ---
        List<PayrollComponentDTO> allowanceList = new ArrayList<>();
        BigDecimal totalAllowanceCalculated = BigDecimal.ZERO;


        Optional<ContractEntity> contractOpt = contractRepo.findActiveContractByUserId(user);

        if (contractOpt.isPresent()) {
            List<ContractallowanceEntity> cAllowances = contractAllowanceRepo.findByContractID(contractOpt.get());

            // Tỷ lệ hưởng lương = Ngày thực tế / 26
            BigDecimal ratio = BigDecimal.ZERO;
            if (standardDays.compareTo(BigDecimal.ZERO) > 0) {
                ratio = actualWorkDays.divide(standardDays, 4, RoundingMode.HALF_UP);
            }

            for (ContractallowanceEntity ca : cAllowances) {
                BigDecimal baseAmount = ca.getAmount();
                // Tiền thực nhận = Mức HĐ * Tỷ lệ
                BigDecimal realAmount = baseAmount.multiply(ratio).setScale(0, RoundingMode.HALF_UP);

                allowanceList.add(new PayrollComponentDTO(
                        ca.getAllowanceName(),
                        realAmount,
                        actualWorkDays.doubleValue(),
                        "Mức chuẩn: " + String.format("%,.0f", baseAmount)
                ));
                totalAllowanceCalculated = totalAllowanceCalculated.add(realAmount);
            }
        }

        //  TÍNH OT (OVERTIME) ---
        List<PayrollComponentDTO> otList = new ArrayList<>();
        BigDecimal totalOtCalculated = BigDecimal.ZERO;

        // Query OT theo ID user
        List<OvertimeRequestEntity> otRequests = otRequestRepo.findApprovedRequestsByMonth(user, month, year);

        for (OvertimeRequestEntity req : otRequests) {
            for (OvertimeRequestDetailEntity detail : req.getDetails()) {
                BigDecimal hours = BigDecimal.valueOf(detail.getHours());
                BigDecimal rate = (detail.getOvertimeTypeID() != null) ? detail.getOvertimeTypeID().getRate() : BigDecimal.ONE;
                String otName = (detail.getOvertimeTypeID() != null) ? detail.getOvertimeTypeID().getOtName() : "OT";

                BigDecimal amount = hours.multiply(rate).multiply(hourlyRate).setScale(0, RoundingMode.HALF_UP);

                otList.add(new PayrollComponentDTO(
                        otName,
                        amount,
                        detail.getHours(),
                        String.format("Ngày %02d/%02d (x%s)", req.getDate().getDayOfMonth(), month, rate)
                ));
                totalOtCalculated = totalOtCalculated.add(amount);
            }
        }

        //  TÍNH THƯỞNG / PHẠT ---
        List<PayrollComponentDTO> bonusList = new ArrayList<>();
        List<PayrollComponentDTO> punishmentList = new ArrayList<>();
        BigDecimal totalBonusCalc = BigDecimal.ZERO;
        BigDecimal totalPunishCalc = BigDecimal.ZERO;

        // Query Thưởng phạt theo ID user
        List<RewardpunishmentdecisionEntity> decisions = rewardRepo.findByMonth(user, startDate, endDate);
        for (RewardpunishmentdecisionEntity dec : decisions) {
            PayrollComponentDTO item = new PayrollComponentDTO(
                    dec.getReason(),
                    dec.getAmount(),
                    1.0,
                    "Ngày QĐ: " + dec.getDecisionDate()
            );

            if (dec.getType() != null && "REWARD".equalsIgnoreCase(dec.getType().name())) {
                bonusList.add(item);
                totalBonusCalc = totalBonusCalc.add(dec.getAmount());
            } else {
                punishmentList.add(item);
                totalPunishCalc = totalPunishCalc.add(dec.getAmount());
            }
        }

        // TỔNG HỢP BẢO HIỂM NHÂN VIÊN ---
        BigDecimal bhxh = payroll.getBhxhEmp() != null ? payroll.getBhxhEmp() : BigDecimal.ZERO;
        BigDecimal bhyt = payroll.getBhytEmp() != null ? payroll.getBhytEmp() : BigDecimal.ZERO;
        BigDecimal bhtn = payroll.getBhtnEmp() != null ? payroll.getBhtnEmp() : BigDecimal.ZERO;
        BigDecimal totalInsuranceEmp = bhxh.add(bhyt).add(bhtn);


        return PayrollDetailDTO.builder()
                .userId(user.getId())
                .fullName(user.getFullname())
                .departmentName(user.getDepartmentID() != null ? user.getDepartmentID().getDepartmentname() : "")
                .jobType(user.getJobtype())
                .payPeriod(payPeriod)
                .status(payroll.getStatus() != null ? payroll.getStatus() : "DRAFT")

                // Work Time
                .baseSalary(baseSalary)
                .standardWorkDays(standardDays)
                .actualWorkDays(actualWorkDays)

                // Income Summary
                .totalAllowance(totalAllowanceCalculated)
                .totalOvertimePay(totalOtCalculated)
                .totalBonus(totalBonusCalc)
                .totalPunishment(totalPunishCalc)
                .totalIncome(payroll.getTotalincome())

                // Tax & Deductions
                .pit(payroll.getPit())
                .bhxhEmp(bhxh)
                .bhytEmp(bhyt)
                .bhtnEmp(bhtn)
                .totalInsuranceEmp(totalInsuranceEmp)

                // Company Cost
                .bhxhComp(payroll.getBhxhComp())
                .bhytComp(payroll.getBhytComp())
                .bhtnComp(payroll.getBhtnComp())

                // FINAL NET
                .netSalary(payroll.getNetsalary())

                // Details Lists
                .allowanceDetails(allowanceList)
                .overtimeDetails(otList)
                .bonusDetails(bonusList)
                .punishmentDetails(punishmentList)
                .build();
    }


    // Xem phiếu lương của chính mình (API nhận @AuthenticationPrincipal UserEntity)
    public PayrollDetailDTO getMyPayslip(UserEntity user, int month, int year) {
        return getPayrollDetail(user, month, year);
    }

    //  Xuất PDF (API nhận UserEntity)
    public PayrollDetailDTO getMyPayslipPDF(UserEntity user, int month, int year) {
        return getPayrollDetail(user, month, year);
    }

    // Xem lương của nhân viên khác (Dành cho HR/Admin - truyền ID)
    public PayrollDetailDTO getPayrollDetailById(Integer userId, int month, int year) {
        // Tìm UserEntity trước rồi mới gọi hàm Core
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + userId));
        return getPayrollDetail(user, month, year);
    }

    // Danh sách lương (Filter Criteria - Giữ nguyên)
    public Page<PayrollDTO> getPayrollList(PayrollFilterCriteria criteria, Pageable pageable) {
        Specification<PayrollEntity> spec = PayrollFilter.filterBy(criteria);
        Page<PayrollEntity> pageResult = payrollRepository.findAll(spec, pageable);

        return pageResult.map(entity -> PayrollDTO.builder()
                .userId(entity.getUserID().getId())
                .fullName(entity.getUserID().getFullname())
                .departmentName(entity.getUserID().getDepartmentID() != null ? entity.getUserID().getDepartmentID().getDepartmentname() : "")
                .jobType(entity.getUserID().getJobtype())
                .payPeriod(entity.getPayperiod())
                .baseSalary(entity.getBasesalarysnapshot())
                .actualWorkDays(entity.getActualworkdays())
                .totalIncome(entity.getTotalincome())
                .netSalary(entity.getNetsalary())
                .bhxhEmp(entity.getBhxhEmp())
                .bhytEmp(entity.getBhytEmp())
                .bhtnEmp(entity.getBhtnEmp())
                .bhxhComp(entity.getBhxhComp())
                .bhytComp(entity.getBhytComp())
                .bhtnComp(entity.getBhtnComp())
                .pit(entity.getPit())
                .build()
        );
    }

    public Map<String, Object> convertDtoToPdfData(PayrollDetailDTO dto) {
        Map<String, Object> data = new HashMap<>();

     // header
        Map<String, Object> header = new HashMap<>();
        header.put("username", String.valueOf(dto.getUserId()));
        header.put("fullname", dto.getFullName());
        header.put("departmentname", dto.getDepartmentName());
        header.put("payperiod", dto.getPayPeriod());
        header.put("totalincome", dto.getTotalIncome());
        header.put("netsalary", dto.getNetSalary());

        data.put("header", header);

        // Chi tiết thu nhập & khấu trừ
        List<Map<String, Object>> items = new ArrayList<>();


        items.add(createPdfItem("Lương cơ bản / Base Salary", dto.getBaseSalary()));

        if (dto.getAllowanceDetails() != null) {
            for (PayrollComponentDTO comp : dto.getAllowanceDetails()) {
                items.add(createPdfItem(comp.getName(), comp.getAmount()));
            }
        }

        //  Tăng ca (OT)
        if (dto.getOvertimeDetails() != null) {
            for (PayrollComponentDTO comp : dto.getOvertimeDetails()) {
                items.add(createPdfItem("OT: " + comp.getName(), comp.getAmount()));
            }
        }

        //Thưởng
        if (dto.getBonusDetails() != null) {
            for (PayrollComponentDTO comp : dto.getBonusDetails()) {
                items.add(createPdfItem("Thưởng: " + comp.getName(), comp.getAmount()));
            }
        }

        // Phạt
        if (dto.getPunishmentDetails() != null) {
            for (PayrollComponentDTO comp : dto.getPunishmentDetails()) {
                items.add(createPdfItem("Phạt: " + comp.getName(), comp.getAmount()));
            }
        }

        // -- KHẤU TRỪ: Bảo hiểm
        items.add(createPdfItem("Bảo hiểm Xã hội (BHXH)", dto.getBhxhEmp()));
        items.add(createPdfItem("Bảo hiểm Y tế (BHYT)", dto.getBhytEmp()));
        items.add(createPdfItem("Bảo hiểm Thất nghiệp (BHTN)", dto.getBhtnEmp()));

        // -- KHẤU TRỪ: Thuế
        items.add(createPdfItem("Thuế TNCN / Personal Income Tax", dto.getPit()));

        data.put("items", items);

        return data;
    }

    // Helper tạo map con cho từng dòng
    private Map<String, Object> createPdfItem(String name, BigDecimal value) {
        Map<String, Object> item = new HashMap<>();
        item.put("item_name", name);
        item.put("item_value", value != null ? value : BigDecimal.ZERO);
        return item;
    }
}