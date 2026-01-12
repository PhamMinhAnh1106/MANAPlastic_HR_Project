import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type Theme = 'light' | 'dark';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    private themeSubject = new BehaviorSubject<Theme>(this.getInitialTheme());
    public theme$: Observable<Theme> = this.themeSubject.asObservable();

    constructor() {
        this.applyTheme(this.getInitialTheme());
    }

    private getInitialTheme(): Theme {
        const savedTheme = sessionStorage.getItem('theme') as Theme;
        if (savedTheme === 'light' || savedTheme === 'dark') {
            return savedTheme;
        }
        return 'light'; // Default to light theme
    }

    getCurrentTheme(): Theme {
        return this.themeSubject.value;
    }

    toggleTheme(): void {
        const newTheme: Theme = this.themeSubject.value === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
    }

    setTheme(theme: Theme): void {
        this.themeSubject.next(theme);
        sessionStorage.setItem('theme', theme);
        this.applyTheme(theme);
    }

    private applyTheme(theme: Theme): void {
        if (theme === 'dark') {
            document.documentElement.classList.add('dark-mode');
            document.body.classList.add('dark-mode');
        } else {
            document.documentElement.classList.remove('dark-mode');
            document.body.classList.remove('dark-mode');
        }
    }
}

