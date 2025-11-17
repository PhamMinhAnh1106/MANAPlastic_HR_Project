export interface leaverequests {
    leaverequestID: number,
    username: string
    fullname: string
    leavetype: string
    startdate: string
    enddate: string
    reason: string
    status: string
    requestdate: string
}
export interface leaverequestRegister {
    leavetype: string
    startdate: string
    enddate: string
    reason: string
}
export interface leaverequestBalance {
    leaveType: string
    leaveTypeId: number
    year: number
    totalGranted: number
    carriedOver: number
    daysUsed: number
    remaining: number
}