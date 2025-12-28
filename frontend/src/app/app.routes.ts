import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Information } from './pages/user/information/information';
import { ChangePassword } from './pages/user/change-password/change-password';
import { AddAccount } from './pages/features/admin/add-account/add-account';
import { Accounts } from './pages/features/hr/accounts/accounts';
import { Attendant } from './pages/features/hr/attendant/attendant';
import { Schedule } from './pages/features/manager/schedule/schedule';
import { RegisterSchedule } from './pages/features/manager/register-schedule/register-schedule';
import { Tablemonth } from './pages/shared/tablemonth/tablemonth';
import { Leaverequests } from './pages/features/employee/leaverequests/leaverequests';
import { ADdleaverequest } from './pages/features/employee/addleaverequest/addleaverequest';
import { Leaverequestcheck } from './pages/features/hr/leaverequestcheck/leaverequestcheck';
import { ForgetPassword } from './pages/forget-password/forget-password';
import { Contracts } from './pages/features/hr/contracts/contracts';
import { ContractsAdd } from './pages/features/hr/contracts-add/contracts-add';
import { AutoSchedule } from './pages/features/manager/auto-schedule/auto-schedule';
import { Payrollmain } from './pages/features/hr/payroll/payrollmain/payrollmain';
import { Payrollrules } from './pages/features/hr/payroll/payrollrules/payrollrules';
import { Payrollpayslip } from './pages/features/hr/payroll/payrollpayslip/payrollpayslip';
import { Permissions } from './pages/features/admin/permissions/permissions';
import { Filterpayslip } from './pages/features/hr/payroll/filterpayslip/filterpayslip';
import { Main } from './pages/features/admin/legal/main/main';
import { ActivityLogs } from './pages/features/admin/activity-logs/activity-logs';
import { DataAttendant } from './pages/features/hr/data-attendant/data-attendant';


export const routes: Routes = [
    {
        path: 'home', component: Home,
        children: [
            {
                path: 'info', component: Information,
                children: [
                    { path: 'user', component: Information },
                ]
            },
            { path: 'changepassword', component: ChangePassword },

            //admin
            { path: 'add/account', component: AddAccount },
            { path: 'permission', component: Permissions },
            { path: 'law', component: Main },
            { path: 'activity-logs', component: ActivityLogs },

            //hr
            { path: 'user/account', component: Accounts },
            { path: 'user/attendance', component: Attendant },
            { path: 'user/attendance/data', component: DataAttendant },

            { path: 'payroll', component: Payrollmain },
            { path: 'payroll/rules', component: Payrollrules },
            { path: 'payroll/payslip', component: Payrollpayslip },
            { path: 'payroll/payslip/filter', component: Filterpayslip },
            //manager 
            { path: 'leaverequest/manage', component: Leaverequestcheck },
            { path: 'contracts', component: Contracts },
            { path: 'contracts/add', component: ContractsAdd },


            //employee
            {
                path: 'calender', component: Tablemonth,
            },
            { path: 'schedule/register', component: RegisterSchedule },
            { path: 'schedule/auto', component: AutoSchedule },
            { path: 'schedule', component: Schedule },
            { path: 'leaverequest', component: Leaverequests },
            { path: 'leaverequest/add', component: ADdleaverequest },



        ]
    },
    { path: 'login', component: Login },
    { path: 'forgotpassword', component: ForgetPassword },

    //back ve home khi ko co tro toi trang nao
    { path: '', redirectTo: '/home/info', pathMatch: 'full' },
    { path: '**', redirectTo: '/home/info' },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }