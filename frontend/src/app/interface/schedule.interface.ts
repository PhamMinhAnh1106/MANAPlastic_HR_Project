export interface schedule {
    date: string,
    shiftId: number | null,
    shiftName?: string | null,
    isDayOff: boolean
}

export interface userSchedule {
    employeeId: number,
    employeeFullName: string,
    drafts: schedule[]
    selectedDraft?: {
        shiftId: number;
        shiftName: string;
        date: string;
    } | null;
}

export interface ChangeSchedule extends schedule {
    employeeId: number
}

export interface ShiftChangeRequest {
    id: number;
    employeeId: number;
    employeeName: string;
    departmentName: string;

    currentShiftName: string;
    newShiftName: string;

    targetDate: string;      // ISO date: "2025-12-31"
    createdAt: string;       // ISO datetime: "2025-12-14T05:24:11Z"

    reason: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';

    approverName: string | null;
}
