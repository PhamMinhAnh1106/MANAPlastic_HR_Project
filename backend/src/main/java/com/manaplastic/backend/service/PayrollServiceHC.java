//package com.manaplastic.backend.service;
//
//import com.manaplastic.backend.DTO.payroll.PayrollDTO;
//import com.manaplastic.backend.entity.*;
//import com.manaplastic.backend.repository.*;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//public class PayrollServiceHC {
//
//    @Autowired private AttendanceRepository attendanceRepo;
//    @Autowired private ContractAllowancesRepository contractAllowancesRepo;
//    @Autowired private ContractRepository contractsRepo;
//    @Autowired private DependentsRepository dependentsRepo;
//    @Autowired private InssuranceSettingRepository insuranceSettingsRepo;
//    @Autowired private MonthlyPayrollConfigsRepository configRepo;
//    @Autowired private OvertimeRepository overtimeRepo;
//    @Autowired private PayrollsRepository payrollsRepo;
//    @Autowired private TaxBracketsRepository taxRepo;
//    @Autowired private TaxSettingRepository taxSettingsRepo;
//    @Autowired private UserRepository usersRepo;
//    @Autowired private RewardPunishmentRepository rewardRepo;
//    @Autowired private SalaryFormulaService formulaService;
//
//
//    // Hàm kích hoạt quy trình tính lương cho toàn bộ nhân viên trong tháng
//    public void calculatePayrollForMonth(int month, int year) {
//        String period = String.format("%d-%02d", year, month);
//        MonthlypayrollconfigEntity config = configRepo.findByMonthAndYear(month, year)
//                .orElseThrow(() -> new RuntimeException("Chưa cấu hình tham số cho tháng " + period));
//
//        List<UserEntity> activeUsers = usersRepo.findAllActiveUsers();
//
//        // Xóa dữ liệu cũ để tính lại từ đầu (tránh duplicate)
//        deleteOldPayrollData(period);
//
//        System.out.println(">>> Bắt đầu tính lương (Dynamic Engine) cho " + activeUsers.size() + " nhân viên...");
//
//        for (UserEntity user : activeUsers) {
//            try {
//                calculateSingleEmployee(user, config, period);
//            } catch (Exception e) {
//                System.err.println("!!! Lỗi tính lương NV " + user.getFullname() + ": " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //Lấy danh sách lương để hiển thị lên bảng (DTO)
//
//    public List<PayrollDTO> getPayrollsByMonth(int month, int year) {
//        String period = String.format("%d-%02d", year, month);
//        return payrollsRepo.findByPayperiod(period).stream().map(this::convertToDTO).toList();
//    }
//
//    @Transactional
//    public void deleteOldPayrollData(String period) {
//        if (payrollsRepo.existsByPayperiod(period)) {
//            payrollsRepo.deleteByPayperiod(period);
//        }
//    }
//
//    // Điều phối việc lấy dữ liệu -> Tính công thức -> Lưu DB
//    private void calculateSingleEmployee(UserEntity user, MonthlypayrollconfigEntity config, String period) {
//        ContractEntity contract = contractsRepo.findActiveContractByUserId(user.getId()).orElse(null);
//        if (contract == null) return;
//
//        LocalDate effectiveDate = config.getCycleEndDate();
//        Map<String, BigDecimal> context = new HashMap<>(); // Context chứa biến để chạy công thức
//
//        // THU THẬP DỮ LIỆU THÔ
//        // Lương & Ngày công
//        double baseSalary = contract.getBasesalary().doubleValue();
//        double standardDays = config.getStandardWorkDays().doubleValue();
//        double actualWorkDays = calculateActualWorkDays(user.getId(), config.getCycleStartDate(), effectiveDate);
//
//        context.put("BASE_SALARY", BigDecimal.valueOf(baseSalary));
//        context.put("STD_DAYS", BigDecimal.valueOf(standardDays));
//        context.put("REAL_WORK_DAYS", BigDecimal.valueOf(actualWorkDays));
//
//        // Phụ cấp
//        AllowanceCalcResult allowanceRes = calculateAllowances(contract, effectiveDate);
//        context.put("TOTAL_ALLOWANCE", BigDecimal.valueOf(allowanceRes.totalAmount));
//        context.put("TAX_FREE_ALLOWANCE", BigDecimal.valueOf(allowanceRes.totalAmount - allowanceRes.taxableAmount));
//
//        // Tăng ca (OT)
//        OvertimeCalcResult otRes = calculateOvertimePay(user.getId(), config.getCycleStartDate(), effectiveDate, baseSalary, standardDays, effectiveDate);
//        context.put("OT_HOURS", BigDecimal.valueOf(otRes.totalHours));
//        context.put("OT_INCOME", BigDecimal.valueOf(otRes.totalOtPay));
//        context.put("OT_TAX_EXEMPT", BigDecimal.valueOf(otRes.taxExemptPart));
//
//        // Bảo hiểm
//        InsuranceCalcResult insuranceRes = calculateInsurance(contract, allowanceRes, effectiveDate);
//        context.put("INSURANCE_SALARY", BigDecimal.valueOf(insuranceRes.insuranceBase));
//        context.put("INSURANCE_AMT", BigDecimal.valueOf(insuranceRes.totalEmpContribution));
//
//        // Thưởng & Phạt
//        RewardPenaltyResult rpRes = calculateRewardPenalty(user.getId(), config.getCycleStartDate(), effectiveDate);
//        context.put("TOTAL_REWARD", BigDecimal.valueOf(rpRes.totalReward));
//        context.put("TOTAL_PENALTY", BigDecimal.valueOf(rpRes.totalPenalty));
//
//        // Cập nhật tổng thu nhập miễn thuế (bao gồm phụ cấp ăn ca + thưởng miễn thuế + chênh lệch OT)
//        BigDecimal currentTaxFree = context.getOrDefault("TAX_FREE_ALLOWANCE", BigDecimal.ZERO);
//        context.put("TAX_FREE_INCOME_TOTAL", currentTaxFree.add(BigDecimal.valueOf(rpRes.taxExemptReward)));
//
//        // Tham số Giảm trừ gia cảnh
//        double personalDeduction = getTaxSettingValue("PERSONAL_DEDUCTION", effectiveDate);
//        double dependentDeduction = getTaxSettingValue("DEPENDENT_DEDUCTION", effectiveDate);
//        long dependentCount = dependentsRepo.countTaxDeductibleDependents(user.getId());
//
//        context.put("PERSONAL_DEDUCTION", BigDecimal.valueOf(personalDeduction));
//        context.put("DEPENDENT_DEDUCTION", BigDecimal.valueOf(dependentDeduction));
//        context.put("DEPENDENT_COUNT", BigDecimal.valueOf(dependentCount));
//
//        // CHẠY CÔNG THỨC ĐỘNG (DYNAMIC FORMULAS)
//        // Tính: TOTAL_INCOME, TAXABLE_INCOME dựa trên cấu hình trong DB
//        context = formulaService.calculateDynamicPayroll(context);
//
//        // TÍNH THUẾ & NET (FINAL CALCULATION) ---
//        BigDecimal totalIncome = context.getOrDefault("TOTAL_INCOME", BigDecimal.ZERO);
//        BigDecimal taxableIncome = context.getOrDefault("TAXABLE_INCOME", BigDecimal.ZERO);
//
//        // Thuế TNCN lũy tiến (Logic phức tạp nên tính bằng Java) - Lũy tiến 7 bậc
//        if (taxableIncome.doubleValue() < 0) taxableIncome = BigDecimal.ZERO;
//        double pitAmount = calculatePitByProgressiveTable(taxableIncome.doubleValue());
//        context.put("PIT_TAX", BigDecimal.valueOf(pitAmount));
//
//        // Tính Lương NET = Tổng thu nhập - BH - Thuế - Phạt
//        BigDecimal netSalary = totalIncome
//                .subtract(BigDecimal.valueOf(insuranceRes.totalEmpContribution))
//                .subtract(BigDecimal.valueOf(pitAmount))
//                .subtract(BigDecimal.valueOf(rpRes.totalPenalty));
//
//        // LƯU VÀO DB
//        // Thu nhập chịu thuế (Assessable) = Thu nhập tính thuế + Các khoản giảm trừ
//        double totalDeductions = personalDeduction + (dependentCount * dependentDeduction) + insuranceRes.totalEmpContribution;
//        double assessableIncome = taxableIncome.doubleValue() + totalDeductions;
//
//        savePayrollRecord(user, period, contract,
//                totalIncome.doubleValue(),
//                insuranceRes,
//                assessableIncome,
//                taxableIncome.doubleValue(),
//                pitAmount,
//                netSalary.doubleValue(),
//                rpRes,
//                actualWorkDays, standardDays, otRes.totalOtPay, allowanceRes.totalAmount,
//                otRes.taxExemptPart);
//    }
//
//
//    private double calculateActualWorkDays(int userId, LocalDate fromDate, LocalDate toDate) {
//        Double days = attendanceRepo.countPresentDays(userId, fromDate, toDate);
//        return (days != null) ? days : 0.0;
//    }
//
//    private AllowanceCalcResult calculateAllowances(ContractEntity contract, LocalDate effectiveDate) {
//        List<ContractallowanceEntity> allowList = contractAllowancesRepo.findByContractID(contract);
//        AllowanceCalcResult result = new AllowanceCalcResult();
//        double lunchLimit = getTaxSettingValue("LUNCH_ALLOWANCE_LIMIT", effectiveDate);
//
//        for (ContractallowanceEntity ca : allowList) {
//            double amount = ca.getAmount() != null ? ca.getAmount().doubleValue() : 0.0;
//            double taxFree = ca.getTaxFreeAmount() != null ? ca.getTaxFreeAmount().doubleValue() : 0.0;
//            String type = ca.getAllowanceType() != null ? ca.getAllowanceType().toUpperCase() : "OTHER";
//
//            // Tự động áp dụng mức miễn thuế cho tiền ăn
//            if (taxFree <= 0 && "MEAL".equals(type)) {
//                taxFree = lunchLimit;
//            }
//
//            result.totalAmount += amount;
//            double taxablePart = Math.max(0, amount - taxFree);
//            result.taxableAmount += taxablePart;
//
//            if (Boolean.TRUE.equals(ca.getIsInsuranceBase())) {
//                result.insuranceBaseAmount += amount;
//            }
//        }
//        return result;
//    }
//
//    private OvertimeCalcResult calculateOvertimePay(int userId, LocalDate fromDate, LocalDate toDate,
//                                                    double baseSalary, double stdDays, LocalDate effectiveDate) {
//        List<OvertimeEntity> otList = overtimeRepo.findApprovedOvertime(userId, fromDate, toDate);
//        OvertimeCalcResult res = new OvertimeCalcResult();
//        double hoursPerDay = getTaxSettingValue("HOURS_PER_WORKDAY", effectiveDate);
//        double hourlyRate = (baseSalary / stdDays) / hoursPerDay;
//
//        for (OvertimeEntity ot : otList) {
//            OvertimetypeEntity t = ot.getOvertimetypeid();
//            if (t == null) continue;
//
//            double hours = ot.getHours();
//            res.totalHours += hours;
//
//            double rate = t.getRate() != null ? t.getRate().doubleValue() : 1.0;
//            String calcType = t.getCalculationType() != null ? t.getCalculationType().toUpperCase() : "MULTIPLIER";
//
//            double otPay = 0.0;
//            switch (calcType) {
//                case "MULTIPLIER" -> otPay = hours * hourlyRate * rate;
//                case "ADDITIVE" -> otPay = hours * hourlyRate * rate;
//                case "FIXED_AMOUNT" -> otPay = hours * rate;
//                default -> otPay = hours * hourlyRate * rate;
//            }
//
//            res.totalOtPay += otPay;
//            res.taxExemptPart += calculateTaxExemptPart(otPay, hours, hourlyRate, t);
//        }
//        return res;
//    }
//
//    private RewardPenaltyResult calculateRewardPenalty(int userId, LocalDate fromDate, LocalDate toDate) {
//        RewardPenaltyResult res = new RewardPenaltyResult();
//        List<RewardpunishmentdecisionEntity> list = rewardRepo.findApprovedDecisions(userId, fromDate, toDate);
//
//        for (RewardpunishmentdecisionEntity decision : list) {
//            double amount = decision.getAmount() != null ? decision.getAmount().doubleValue() : 0.0;
//            String type = decision.getType() != null ? decision.getType().toUpperCase() : "";
//            boolean isTaxExempt = Boolean.TRUE.equals(decision.getIsTaxExempt());
//
//            if ("REWARD".equals(type)) {
//                res.totalReward += amount;
//                if (isTaxExempt) res.taxExemptReward += amount;
//            } else if ("PUNISHMENT".equals(type)) {
//                res.totalPenalty += amount;
//            }
//        }
//        return res;
//    }
//
//    private InsuranceCalcResult calculateInsurance(ContractEntity contract, AllowanceCalcResult allowanceRes, LocalDate effectiveDate) {
//        InsuranceCalcResult result = new InsuranceCalcResult();
//        result.insuranceBase = (contract.getInsuranceSalary() != null ? contract.getInsuranceSalary().doubleValue() : 0.0)
//                + allowanceRes.insuranceBaseAmount;
//
//        List<InsurancesettingEntity> settings = insuranceSettingsRepo.findAllActiveSettings(effectiveDate);
//        double generalCap = getInsuranceCap(effectiveDate);
//        double unemploymentCap = getUnemploymentCap(effectiveDate);
//
//        for (InsurancesettingEntity setting : settings) {
//            double empRate = setting.getEmployeeRate() != null ? setting.getEmployeeRate().doubleValue() / 100.0 : 0.0;
//            double compRate = setting.getCompanyRate() != null ? setting.getCompanyRate().doubleValue() / 100.0 : 0.0;
//
//            double baseForCalculation;
//            String capType = setting.getCapType() != null ? setting.getCapType().toUpperCase() : "BASIC_SALARY_CAP";
//
//            switch (capType) {
//                case "REGION_MIN_SALARY_CAP" -> baseForCalculation = Math.min(result.insuranceBase, unemploymentCap);
//                case "BASIC_SALARY_CAP" -> baseForCalculation = Math.min(result.insuranceBase, generalCap);
//                case "CUSTOM" -> baseForCalculation = (setting.getCappedSalary() != null)
//                        ? Math.min(result.insuranceBase, setting.getCappedSalary().doubleValue())
//                        : result.insuranceBase;
//                case "NO_CAP" -> baseForCalculation = result.insuranceBase;
//                default -> baseForCalculation = Math.min(result.insuranceBase, generalCap);
//            }
//
//            double empAmount = baseForCalculation * empRate;
//            double compAmount = baseForCalculation * compRate;
//
//            result.totalEmpContribution += empAmount;
//            result.totalCompContribution += compAmount;
//
//            String type = (setting.getType() != null) ? setting.getType().toUpperCase() : "";
//            result.empDetails.put(type, empAmount);
//            result.compDetails.put(type, compAmount);
//        }
//        return result;
//    }
//
//    private double calculateTaxExemptPart(double otPay, double hours, double hourlyRate, OvertimetypeEntity otType) {
//        String formula = otType.getTaxExemptFormula() != null ? otType.getTaxExemptFormula().toUpperCase() : "NONE";
//        return switch (formula) {
//            case "EXCESS_ONLY" -> Math.max(0.0, otPay - (hours * hourlyRate)); // Chỉ miễn phần vượt (150% - 100%)
//            case "FULL_AMOUNT" -> otPay;
//            case "PERCENTAGE" -> {
//                BigDecimal percent = otType.getTaxExemptPercentage();
//                yield (percent != null) ? otPay * (percent.doubleValue() / 100.0) : 0.0;
//            }
//            default -> 0.0;
//        };
//    }
//
//    private double calculatePitByProgressiveTable(double taxableIncome) {
//        if (taxableIncome <= 0) return 0;
//        List<TaxbracketEntity> brackets = taxRepo.findAllByOrderByLevelAsc();
//        for (TaxbracketEntity b : brackets) {
//            double maxIncome = (b.getMaxIncome() == null) ? Double.MAX_VALUE : b.getMaxIncome().doubleValue();
//            if (taxableIncome <= maxIncome) {
//                double rate = b.getTaxRate().doubleValue() / 100.0;
//                double subtraction = b.getSubtractionAmount() != null ? b.getSubtractionAmount().doubleValue() : 0.0;
//                return (taxableIncome * rate) - subtraction;
//            }
//        }
//        return 0;
//    }
//
//    private double getTaxSettingValue(String key, LocalDate effectiveDate) {
//        Optional<TaxsettingEntity> opt = taxSettingsRepo.findActiveSettingByKey(key, effectiveDate);
//        return opt.map(s -> s.getValue().doubleValue())
//                .orElseThrow(() -> new RuntimeException("Chưa cấu hình tham số thuế: " + key));
//    }
//    private double getBasicSalaryState(LocalDate date) { return getTaxSettingValue("BASIC_SALARY_STATE", date); }
//    private double getRegionMinSalary(LocalDate date) { return getTaxSettingValue("REGION_MIN_SALARY", date); }
//    private double getInsuranceCapMultiplier(LocalDate date) { return getTaxSettingValue("INSURANCE_CAP_MULTIPLIER", date); }
//    private double getInsuranceCap(LocalDate date) { return getBasicSalaryState(date) * getInsuranceCapMultiplier(date); }
//    private double getUnemploymentCap(LocalDate date) { return getRegionMinSalary(date) * getInsuranceCapMultiplier(date); }
//
//    private void savePayrollRecord(UserEntity user, String period, ContractEntity contract,
//                                   double totalIncome, InsuranceCalcResult insuranceRes, double assessable,
//                                   double taxable, double pit, double net, RewardPenaltyResult rpRes,
//                                   double workDays, double stdDays, double otPay, double allowance,
//                                   double otTaxExemptPart) {
//
//        PayrollEntity p = new PayrollEntity();
//        p.setUserID(user);
//        p.setPayperiod(period);
//        p.setBasesalary(contract.getBasesalary());
//        p.setActualworkdays(workDays);
//        p.setTotalallowance(BigDecimal.valueOf(allowance));
//        p.setTotalovertimepay(BigDecimal.valueOf(otPay));
//        p.setOtTaxExempt(BigDecimal.valueOf(otTaxExemptPart));
//
//        p.setTotalincome(BigDecimal.valueOf(totalIncome));
//        p.setTotalinsuranceemployee(BigDecimal.valueOf(insuranceRes.totalEmpContribution));
//        p.setAssessableincome(BigDecimal.valueOf(assessable));
//        p.setTaxableincome(BigDecimal.valueOf(taxable));
//        p.setPit(BigDecimal.valueOf(pit));
//        p.setNetsalary(BigDecimal.valueOf(net));
//
//        // Lưu thưởng phạt
//        p.setBonus(BigDecimal.valueOf(rpRes.totalReward));
//        p.setPenalty(BigDecimal.valueOf(rpRes.totalPenalty));
//
//        // Lưu chi tiết bảo hiểm
//        p.setInsurancebase(BigDecimal.valueOf(insuranceRes.insuranceBase));
//        p.setBhxhEmp(BigDecimal.valueOf(insuranceRes.empDetails.getOrDefault("SOCIAL", 0.0)));
//        p.setBhytEmp(BigDecimal.valueOf(insuranceRes.empDetails.getOrDefault("HEALTH", 0.0)));
//        p.setBhtnEmp(BigDecimal.valueOf(insuranceRes.empDetails.getOrDefault("UNEMPLOYMENT", 0.0)));
//        p.setBhxhComp(BigDecimal.valueOf(insuranceRes.compDetails.getOrDefault("SOCIAL", 0.0)));
//        p.setBhytComp(BigDecimal.valueOf(insuranceRes.compDetails.getOrDefault("HEALTH", 0.0)));
//        p.setBhtnComp(BigDecimal.valueOf(insuranceRes.compDetails.getOrDefault("UNEMPLOYMENT", 0.0)));
//
//        payrollsRepo.save(p);
//    }
//
//    private PayrollDTO convertToDTO(PayrollEntity entity) {
//        PayrollDTO dto = new PayrollDTO();
//        UserEntity user = entity.getUserID();
//
//        dto.setUserId(user.getId());
//        dto.setFullName(user.getFullname());
//        dto.setJobType(user.getJobtype());
//        dto.setDepartmentName(user.getDepartmentID() != null ? user.getDepartmentID().getDepartmentname() : "N/A");
//
//        dto.setPayPeriod(entity.getPayperiod());
//        dto.setBaseSalary(entity.getBasesalary());
//        dto.setActualWorkDays(entity.getActualworkdays());
//        dto.setTotalOvertimePay(entity.getTotalovertimepay());
//        dto.setTotalAllowance(entity.getTotalallowance());
//        dto.setTotalIncome(entity.getTotalincome());
//
//        dto.setInsuranceEmp(entity.getTotalinsuranceemployee());
//        dto.setTaxableIncome(entity.getTaxableincome());
//        dto.setPit(entity.getPit());
//        dto.setNetSalary(entity.getNetsalary());
//
//        // Chi tiết
//        dto.setInsuranceBase(entity.getInsurancebase());
//        dto.setBhxhEmp(entity.getBhxhEmp());
//        dto.setBhytEmp(entity.getBhytEmp());
//        dto.setBhtnEmp(entity.getBhtnEmp());
//        dto.setBhxhComp(entity.getBhxhComp());
//        dto.setBhytComp(entity.getBhytComp());
//        dto.setBhtnComp(entity.getBhtnComp());
//        dto.setOtTaxExempt(entity.getOtTaxExempt());
//
//        return dto;
//    }
//
//    private static class InsuranceCalcResult {
//        double insuranceBase = 0.0;
//        double totalEmpContribution = 0.0;
//        double totalCompContribution = 0.0;
//        Map<String, Double> empDetails = new HashMap<>();
//        Map<String, Double> compDetails = new HashMap<>();
//    }
//    private static class AllowanceCalcResult {
//        double totalAmount = 0;
//        double taxableAmount = 0;
//        double insuranceBaseAmount = 0;
//    }
//    private static class OvertimeCalcResult {
//        double totalHours = 0;
//        double totalOtPay = 0;
//        double taxExemptPart = 0;
//    }
//    private static class RewardPenaltyResult {
//        double totalReward = 0;
//        double totalPenalty = 0;
//        double taxExemptReward = 0;
//    }
//}