package com.manaplastic.backend.constant.permission;


public final class PermissionConst {
    private PermissionConst() {} // Prevent instantiation

    // --- A. NHÓM CHUNG ---
    public static final String LOGIN = "LOGIN";
    public static final String CHANGE_PASS = "CHANGE_PASS";
    public static final String PROFILE_VIEW = "PROFILE_VIEW";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";

    // --- B. QUẢN LÝ TÀI KHOẢN ---
    public static final String ACCOUNT_VIEW_LIST = "ACCOUNT_VIEW_LIST";
    public static final String ACCOUNT_VIEW_DETAIL = "ACCOUNT_VIEW_DETAIL";
    public static final String ACCOUNT_CREATE = "ACCOUNT_CREATE";
    public static final String ACCOUNT_UPDATE = "ACCOUNT_UPDATE";
    public static final String ACCOUNT_DELETE = "ACCOUNT_DELETE";
    public static final String ACCOUNT_RESET_PASS = "ACCOUNT_RESET_PASS";
    public static final String ACCOUNT_PERMISSION = "ACCOUNT_PERMISSION";

    // --- D. QUẢN LÝ CHẤM CÔNG ---
    public static final String ATTENDANCE_VIEW_SELF = "ATTENDANCE_VIEW_SELF";
    public static final String ATTENDANCE_REQ_CREATE = "ATTENDANCE_REQ_CREATE";
    public static final String ATTENDANCE_VIEW_DEPT = "ATTENDANCE_VIEW_DEPT";
    public static final String ATTENDANCE_VIEW_ALL = "ATTENDANCE_VIEW_ALL";
    public static final String ATTENDANCE_UPDATE = "ATTENDANCE_UPDATE";
    public static final String ATTENDANCE_APPROVE = "ATTENDANCE_APPROVE";
    public static final String ATTENDANCE_EXPORT = "ATTENDANCE_EXPORT";
    public static final String SHIFT_VIEW = "SHIFT_VIEW";
    public static final String SHIFT_ASSIGN = "SHIFT_ASSIGN";
    public static final String SHIFT_REGISTER = "SHIFT_REGISTER";

    // --- E. QUẢN LÝ NGHỈ PHÉP ---
    public static final String LEAVE_CREATE = "LEAVE_CREATE";
    public static final String LEAVE_VIEW_SELF = "LEAVE_VIEW_SELF";
    public static final String LEAVE_CANCEL = "LEAVE_CANCEL";
    public static final String LEAVE_VIEW_DEPT = "LEAVE_VIEW_DEPT";
    public static final String LEAVE_VIEW_ALL = "LEAVE_VIEW_ALL";
    public static final String LEAVE_APPROVE = "LEAVE_APPROVE";

    // --- F. QUẢN LÝ LƯƠNG ---
    public static final String PAYROLL_VIEW_SELF = "PAYROLL_VIEW_SELF";
    public static final String PAYROLL_CALCULATE = "PAYROLL_CALCULATE";
    public static final String PAYROLL_VIEW_ALL = "PAYROLL_VIEW_ALL";
    public static final String PAYROLL_REWARD_VIEW = "PAYROLL_REWARD_VIEW";
    public static final String PAYROLL_EXPORT = "PAYROLL_EXPORT";

    // --- G. QUẢN LÝ HỢP ĐỒNG ---
    public static final String CONTRACT_VIEW = "CONTRACT_VIEW";
    public static final String CONTRACT_CREATE = "CONTRACT_CREATE";
    public static final String CONTRACT_UPDATE = "CONTRACT_UPDATE";
    public static final String CONTRACT_EXPORT = "CONTRACT_EXPORT";

    // --- H. ĐÁNH GIÁ ---
    public static final String EVALUATE_VIEW = "EVALUATE_VIEW";
    public static final String EVALUATE_CREATE = "EVALUATE_CREATE";

    // --- I. OT ---
    public static final String OVERTIME_VIEW = "OVERTIME_VIEW";
    public static final String OVERTIME_VIEW_DETAIL = "OVERTIME_VIEW_DETAIL";
    public static final String OVERTIME_CREATE = "OVERTIME_CREATE";
    public static final String OVERTIME_UPDATE = "OVERTIME_UPDATE";
    public static final String OVERTIME_DELETE = "OVERTIME_DELETE";

    public static final String OVERTIME_APPROVE_MANAGER = "OVERTIME_APPROVE_MANAGER";
    public static final String OVERTIME_APPROVE_HR = "OVERTIME_APPROVE_HR";
    public static final String OVERTIME_REJECT = "OVERTIME_REJECT";

}