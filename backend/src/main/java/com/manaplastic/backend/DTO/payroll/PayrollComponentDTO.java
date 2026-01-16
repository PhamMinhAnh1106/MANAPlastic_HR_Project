package com.manaplastic.backend.DTO.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollComponentDTO {
    private String name;        // Tên hiển thị (VD: Phụ cấp cơm, Tăng ca ngày thường)
    private BigDecimal amount;  // Số tiền
    private Double quantity;    // Số lượng (VD: số giờ OT, số ngày công) - Optional
    private String note;        // Ghi chú (VD: Lý do thưởng/phạt)
}