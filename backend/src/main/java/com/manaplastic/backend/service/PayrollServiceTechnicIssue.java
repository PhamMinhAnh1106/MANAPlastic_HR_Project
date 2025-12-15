//package com.manaplastic.backend.service;
//
//import com.manaplastic.backend.DTO.payroll.PayrollDTO;
//import com.manaplastic.backend.DTO.criteria.PayrollFilterCriteria;
//import com.manaplastic.backend.entity.*;
//import com.manaplastic.backend.filters.PayrollFilter;
//import com.manaplastic.backend.repository.*;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class PayrollServiceTechnicIssue {
//
//    @Autowired
//    private UserRepository usersRepo;
//    @Autowired
//    private MonthlyPayrollConfigsRepository configRepo;
//    @Autowired
//    private PayrollsRepository payrollsRepo;
//    @Autowired
//    private TaxBracketsRepository taxRepo;
//    @Autowired
//    private SalaryVariableRepository variableRepo;
//    @Autowired
//    private SalaryFormulaService formulaService;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public void calculatePayrollForMonth(int month, int year) {
//        String period = String.format("%d-%02d", year, month);
//        MonthlypayrollconfigEntity config = configRepo.findByMonthAndYear(month, year)
//                .orElseThrow(() -> new RuntimeException("Chưa cấu hình tham số cho tháng " + period));
//        List<UserEntity> activeUsers = usersRepo.findAllActiveUsers();
//        List<SalaryvariableEntity> dynamicVariables = variableRepo.findAllBySqlQueryIsNotNull();
//        deleteOldPayrollData(period);
//
//        System.out.println(">>> Bắt đầu Universal Payroll Engine cho " + activeUsers.size() + " nhân viên...");
//
//        for (UserEntity user : activeUsers) {
//            try {
//                calculateSingleEmployee(user, config, dynamicVariables, period);
//            } catch (Exception e) {
//                System.err.println("!!! Lỗi tính lương NV " + user.getFullname() + ": " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //    public List<PayrollDTO> getPayrollsByMonth(int month, int year) {
////        String period = String.format("%d-%02d", year, month);
////        return payrollsRepo.findByPayperiod(period).stream().map(this::mapToDTO).toList();
////    }
//    public List<PayrollDTO> getPayrollsByFilter(PayrollFilterCriteria criteria) {
//        List<PayrollEntity> entities = payrollsRepo.findAll(PayrollFilter.filterBy(criteria));
//        return entities.stream().map(this::mapToDTO).toList();
//    }
//
//    @Transactional
//    public void deleteOldPayrollData(String period) {
//        if (payrollsRepo.existsByPayperiod(period)) {
//            payrollsRepo.deleteByPayperiod(period);
//        }
//    }
//
//    private void calculateSingleEmployee(UserEntity user,
//                                         MonthlypayrollconfigEntity config,
//                                         List<SalaryvariableEntity> dynamicVariables,
//                                         String period) {
//
//        Map<String, BigDecimal> context = new HashMap<>();
//
//        for (SalaryvariableEntity var : dynamicVariables) {
//            BigDecimal value = executeDynamicSql(var.getSQLQuery(), user.getId(), config);
//            context.put(var.getCode(), value);
//        }
//
//        context = formulaService.calculateDynamicPayroll(context);
//
//        BigDecimal taxableIncome = context.getOrDefault("TAXABLE_INCOME", BigDecimal.ZERO);
//        if (taxableIncome.doubleValue() < 0) taxableIncome = BigDecimal.ZERO;
//
//        double pitAmount = calculatePitByProgressiveTable(taxableIncome.doubleValue());
//        context.put("PIT_TAX", BigDecimal.valueOf(pitAmount));
//
//        context = formulaService.calculateDynamicPayroll(context);
//
//        savePayrollRecord(user, period, context);
//    }
//
//    private BigDecimal executeDynamicSql(String sql, int userId, MonthlypayrollconfigEntity config) {
//        try {
//            Query query = entityManager.createNativeQuery(sql);
//
//            if (sql.contains(":userId")) query.setParameter("userId", userId);
//            if (sql.contains(":startDate")) query.setParameter("startDate", config.getCycleStartDate());
//            if (sql.contains(":endDate")) query.setParameter("endDate", config.getCycleEndDate());
//            if (sql.contains(":month")) query.setParameter("month", config.getMonth());
//            if (sql.contains(":year")) query.setParameter("year", config.getYear());
//
//            Object result = query.getSingleResult();
//
//            if (result == null) return BigDecimal.ZERO;
//            return new BigDecimal(result.toString());
//
//        } catch (Exception e) {
//            return BigDecimal.ZERO;
//        }
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
//    public PayrollDTO getPayrollDetailForUser(int userId, int month, int year) {
//        String period = String.format("%d-%02d", year, month);
//
//        UserEntity user = usersRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + userId));
//
//        PayrollEntity payroll = payrollsRepo.findByUserIDAndPayperiod(user, period)
//                .orElseThrow(() -> new RuntimeException("Chưa có dữ liệu lương tháng " + period));
//
//        return mapToDTO(payroll);
//    }
//
//    private void savePayrollRecord(UserEntity user, String period, Map<String, BigDecimal> context) {
//        PayrollEntity p = new PayrollEntity();
//        p.setUserID(user);
//        p.setPayperiod(period);
//
//        p.setBasesalary(context.getOrDefault("BASE_SALARY", BigDecimal.ZERO));
//        p.setActualworkdays(context.getOrDefault("REAL_WORK_DAYS", BigDecimal.ZERO).doubleValue());
//        p.setTotalincome(context.getOrDefault("TOTAL_INCOME", BigDecimal.ZERO));
//        p.setTaxableincome(context.getOrDefault("TAXABLE_INCOME", BigDecimal.ZERO));
//        p.setAssessableincome(context.getOrDefault("ASSESSABLE_INCOME", BigDecimal.ZERO));
//        p.setPit(context.getOrDefault("PIT_TAX", BigDecimal.ZERO));
//        p.setNetsalary(context.getOrDefault("NET_SALARY", BigDecimal.ZERO));
//
//        p.setTotalallowance(context.getOrDefault("TOTAL_ALLOWANCE", BigDecimal.ZERO));
//        p.setTotalovertimepay(context.getOrDefault("OT_INCOME", BigDecimal.ZERO));
//        p.setOtTaxExempt(context.getOrDefault("OT_TAX_EXEMPT", BigDecimal.ZERO));
//        p.setTotalinsuranceemployee(context.getOrDefault("INSURANCE_AMT", BigDecimal.ZERO));
//
//        p.setBonus(context.getOrDefault("TOTAL_REWARD", BigDecimal.ZERO));
//        p.setPenalty(context.getOrDefault("TOTAL_PENALTY", BigDecimal.ZERO));
//
//        p.setInsurancebase(context.getOrDefault("INSURANCE_SALARY", BigDecimal.ZERO));
//        p.setBhxhEmp(context.getOrDefault("BHXH_EMP", BigDecimal.ZERO));
//        p.setBhytEmp(context.getOrDefault("BHYT_EMP", BigDecimal.ZERO));
//        p.setBhtnEmp(context.getOrDefault("BHTN_EMP", BigDecimal.ZERO));
//
//        p.setBhxhComp(context.getOrDefault("BHXH_COMP", BigDecimal.ZERO));
//        p.setBhytComp(context.getOrDefault("BHYT_COMP", BigDecimal.ZERO));
//        p.setBhtnComp(context.getOrDefault("BHTN_COMP", BigDecimal.ZERO));
//
//        payrollsRepo.save(p);
//    }
//
//    private PayrollDTO mapToDTO(PayrollEntity entity) {
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
//}
//
