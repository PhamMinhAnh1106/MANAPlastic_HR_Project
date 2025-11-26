export interface contracts {
    userId: number;
    contractName: string;
    type: string;
    baseSalary: string;
    insuranceSalary: string;
    allowanceToxicType: string;
    signDate: string;
    startDate: string;
    endDate: string;
    file: string

}
export const statusContract = [
    'DRAFT', 'ACTIVE', 'EXPIRING_SOON', 'EXPIRED', 'TERMINATED', 'HISTORY'
] as const;

export type StatusContract = typeof statusContract[number];
