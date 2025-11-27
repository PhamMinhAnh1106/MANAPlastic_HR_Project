export interface reqAutoSchedule {
    departmentId: string,
    shiftId: string,
    totalStaffNeeded: string,
    rules:
    {
        requiredSkillGrade: string,
        minStaffCount: string
    }[]

}