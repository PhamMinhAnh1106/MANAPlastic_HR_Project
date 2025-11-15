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
}