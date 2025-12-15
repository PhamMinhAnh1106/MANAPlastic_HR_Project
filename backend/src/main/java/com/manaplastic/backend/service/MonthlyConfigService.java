package com.manaplastic.backend.service;

import com.manaplastic.backend.DTO.legalsetting.GenegatedYearlyConfigRequestDTO;
import com.manaplastic.backend.DTO.legalsetting.UpdateMonthlyConfigRequestDTO;
import com.manaplastic.backend.entity.MonthlypayrollconfigEntity;
import com.manaplastic.backend.repository.MonthlyConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonthlyConfigService {

    @Autowired
    private MonthlyConfigRepository repo;


    // tạo chu kỳ ngày tự động
    @Transactional
    public String generateYearlyConfig(GenegatedYearlyConfigRequestDTO req) {
        List<MonthlypayrollconfigEntity> configsToSave = new ArrayList<>();
        int createdCount = 0; // Biến đếm số bản ghi được tạo mới

        for (int month = 1; month <= 12; month++) {
            // Kiểm tra trùng lặp
            if (repo.existsByMonthAndYear(month, req.getYear())) {
                continue; // Bỏ qua nếu tháng này đã tồn tại
            }

            LocalDate startDate;
            LocalDate endDate;

            try {
                if (req.getIsCyclePreviousMonth()) {
                    // Logic: Chu kỳ vắt từ tháng trước
                    int prevMonth = (month == 1) ? 12 : month - 1;
                    int startYear = (month == 1) ? req.getYear() - 1 : req.getYear();

                    startDate = getValidDate(startYear, prevMonth, req.getCycleStartDay());

                    LocalDate targetDateInCurrentMonth = getValidDate(req.getYear(), month, req.getCycleStartDay());
                    endDate = targetDateInCurrentMonth.minusDays(1);
                } else {
                    // Logic: Chu kỳ gọn trong tháng
                    startDate = LocalDate.of(req.getYear(), month, 1);
                    endDate = YearMonth.of(req.getYear(), month).atEndOfMonth();
                }
            } catch (DateTimeException e) {
                throw new RuntimeException("Lỗi ngày tháng tháng " + month + ": " + e.getMessage());
            }

            MonthlypayrollconfigEntity config = new MonthlypayrollconfigEntity();
            config.setMonth(month);
            config.setYear(req.getYear());
            config.setCycleStartDate(startDate);
            config.setCycleEndDate(endDate);
            config.setStandardWorkDays(BigDecimal.valueOf(req.getStandardWorkDays()));

            configsToSave.add(config);
            createdCount++; // Tăng biến đếm
        }
        // repo.saveAll(configsToSave);

        // Lưu tất cả các bản ghi mới
        if (!configsToSave.isEmpty()) {
            repo.saveAll(configsToSave);
        }

        // Logic trả về thông báo như bạn yêu cầu
        if (createdCount == 0) {
            return "Năm " + req.getYear() + " đã được tạo đầy đủ trước đó, không có thay đổi nào.";
        }

        return "Đã tạo thành công " + createdCount + " cấu hình payroll cho năm " + req.getYear();
    }


    // cập nhật / sửa
    @Transactional
    public String updateConfig(int configID, UpdateMonthlyConfigRequestDTO req) {
        MonthlypayrollconfigEntity config = repo.findById(configID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình lương với ID: " + configID));

        // Validate ngày hợp lệ
        if (req.getCycleStartDate() != null && req.getCycleEndDate() != null) {
            if (req.getCycleStartDate().isAfter(req.getCycleEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu chu kỳ phải nhỏ hơn ngày kết thúc.");
            }
            config.setCycleStartDate(req.getCycleStartDate());
            config.setCycleEndDate(req.getCycleEndDate());
        }

        if (req.getStandardWorkDays() != null) {
            if (req.getStandardWorkDays() < 0 || req.getStandardWorkDays() > 32) {
                throw new IllegalArgumentException("Số ngày công chuẩn không hợp lệ (0-32).");
            }
            config.setStandardWorkDays(BigDecimal.valueOf(req.getStandardWorkDays()));
        }

        repo.save(config);

        // Trả về thông báo thành công thay vì Entity
        return "Cập nhật thành công cấu hình cho tháng " + config.getMonth() + "/" + config.getYear();
    }

    // Các hàm bổ trợ helper
    public List<MonthlypayrollconfigEntity> findAll() {
        return repo.findAll();
    }

    public List<MonthlypayrollconfigEntity> findByYear(int year) {
        return repo.findByYear(year);
    }

    public List<MonthlypayrollconfigEntity> getConfigFilter(Integer year, Integer month) {
        // Lấy chính xác tháng và năm
        if (year != null && month != null) {
            return repo.findByMonthAndYear(month, year)
                    .map(List::of)
                    .orElse(new ArrayList<>());
        }
        // Chỉ lọc theo năm
            return repo.findByYear(year);
    }

    private LocalDate getValidDate(int year, int month, int dayPreference) {
        YearMonth ym = YearMonth.of(year, month);
        int dayToUse = Math.min(dayPreference, ym.lengthOfMonth());
        return LocalDate.of(year, month, dayToUse);
    }
}