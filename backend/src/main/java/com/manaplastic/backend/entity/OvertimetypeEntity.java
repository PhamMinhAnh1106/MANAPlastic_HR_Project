package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "overtimetypes")
public class OvertimetypeEntity {
    public enum CalculationType {
        MULTIPLIER,     // Nhân hệ số (1.5, 2.0)
        ADDITIVE,       // Cộng thêm % (+30%)
        FIXED_AMOUNT    // Số tiền cố định
    }
    public enum TaxExemptFormula {
        NONE,           // Không miễn thuế
        EXCESS_ONLY,    // Chỉ miễn phần vượt (VD: trả 150%, miễn thuế 50%)
        FULL_AMOUNT,    // Miễn toàn bộ
        PERCENTAGE,     // Miễn theo % cụ thể
        CUSTOM          // Công thức riêng
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OvertimeTypeID", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "OtCode", nullable = false, length = 20)
    private String otCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "OtName", nullable = false, length = 100)
    private String otName;

    @NotNull
    @Column(name = "Rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @ColumnDefault("1")
    @Column(name = "IsTaxExemptPart")
    private Boolean isTaxExemptPart;

    @ColumnDefault("'MULTIPLIER'")
    @Enumerated(EnumType.STRING)
    @Column(name = "CalculationType")
    private CalculationType calculationType;

    @ColumnDefault("'EXCESS_ONLY'")
    @Enumerated(EnumType.STRING)
    @Column(name = "TaxExemptFormula")
    private TaxExemptFormula taxExemptFormula;

    @ColumnDefault("0.00")
    @Column(name = "TaxExemptPercentage", precision = 5, scale = 2)
    private BigDecimal taxExemptPercentage;

    @Lob
    @Column(name = "Description")
    private String description;

}