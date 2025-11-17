import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Information } from './pages/user/information/information';
import { ChangePassword } from './pages/user/change-password/change-password';
import { AddAccount } from './pages/features/admin/add-account/add-account';
import { Accounts } from './pages/features/hr/accounts/accounts';
import { Attendant } from './pages/features/hr/attendant/attendant';
import { InfoBase } from './pages/user/info-base/info-base';
import { Schedule } from './pages/features/manager/schedule/schedule';
import { RegisterSchedule } from './pages/features/manager/register-schedule/register-schedule';
import { Tablemonth } from './pages/shared/tablemonth/tablemonth';


export const routes: Routes = [
    {
        path: 'home', component: Home,
        children: [
            {
                path: 'info', component: InfoBase,
                children: [
                    { path: 'user', component: Information },
                ]
            },
            { path: 'changepassword', component: ChangePassword },
            //admin
            { path: 'add/account', component: AddAccount },
            //hr
            { path: 'user/account', component: Accounts },
            { path: 'user/attendance', component: Attendant },
            //manager 
            //employee
            {
                path: 'calender', component: Tablemonth,
                children: [
                    { path: 'schedule/register', component: RegisterSchedule },
                    { path: 'schedule', component: Schedule },

                ]
            },


        ]
    },
    { path: 'login', component: Login },
    //back ve home khi ko co tro toi trang nao
    { path: '', redirectTo: '/home/info', pathMatch: 'full' },
    { path: '**', redirectTo: '/home/info' },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }