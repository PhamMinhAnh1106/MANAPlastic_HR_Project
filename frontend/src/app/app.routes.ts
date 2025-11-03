import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Information } from './pages/information/information';
import { ChangePassword } from './pages/change-password/change-password';

export const routes: Routes = [
    {
        path: 'home', component: Home,
        children: [
            { path: 'info', component: Information },
            { path: 'changepassword', component: ChangePassword }

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