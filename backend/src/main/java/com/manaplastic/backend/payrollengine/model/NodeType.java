package com.manaplastic.backend.payrollengine.model;


public enum NodeType {
    //  Dữ liệu cơ bản
    CONSTANT,       // Số cố định (Ví dụ: 1000000)
    VARIABLE,       // Biến lấy từ dữ liệu nhân viên (Ví dụ: working_days)
    REFERENCE,      // Tham chiếu đến Rule khác (Ví dụ: tham chiếu đến GROSS_SALARY)

    //  Phép toán số học
    ADD,            // Cộng
    SUB,            // Trừ
    MUL,            // Nhân
    DIV,            // Chia

    // Logic điều kiện (Dùng cho IF/ELSE)
    GT,             // Lớn hơn (Greater Than)
    LT,             // Nhỏ hơn (Less Than)
    GTE,            // Lớn hơn hoặc bằng
    LTE,            // Nhỏ hơn hoặc bằng
    EQ,             // Bằng
    AND,            // Và
    OR,             // Hoặc

    // Cấu trúc điều khiển
    IF_ELSE         // Cấu trúc Nếu... Thì...
}
