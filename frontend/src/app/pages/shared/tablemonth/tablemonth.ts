import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tablemonth',
  imports: [NgFor, FormsModule, NgIf],
  templateUrl: './tablemonth.html',
  styleUrl: './tablemonth.scss',
})
export class Tablemonth implements OnInit {
  selectedDay: any = null;

  months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  years = [2025, 2026];

  public year = 2025;
  public month = 11;
  public weeks: any[] = [];
  mergedDays: any[] = [];

  ngOnInit() {
    this.updateCalendar();
  }

  updateCalendar() {
    this.weeks = this.getCalendarMatrix(this.year, this.month);
  }

  selectDay(day: any) {
    if (!day.inMonth) return;
    this.selectedDay = day;
  }

  setMonth() {
    this.updateCalendar();
  }

  getCalendarMatrix(year: number, month: number) {
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const weeks = [];
    let current = new Date(firstDay);
    current.setDate(current.getDate() - current.getDay());

    while (current <= lastDay || current.getDay() !== 0) {
      let week = [];
      for (let i = 0; i < 7; i++) {
        week.push({
          day: current.getDate(),
          inMonth: current.getMonth() === month - 1,
          dateObj: new Date(current)
        });
        current.setDate(current.getDate() + 1);
      }
      weeks.push(week);
    }
    return weeks;
  }

  formatDateLocal(d: Date) {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getShiftForDay(d: any) {
    if (!this.mergedDays || this.mergedDays.length === 0) return null;
    const dateStr = this.formatDateLocal(d.dateObj);
    return this.mergedDays.find(x => x.date === dateStr)?.shift || null;
  }

  // helper merge schedule nếu muốn dùng trực tiếp
  mergeSchedule(days: any[], apiData: any[]) {
    return days.map(day => {
      const found = apiData.find(x => x.date === day.date);
      return { ...day, shift: found ? found : null };
    });
  }

  getAllDays() {
    const result: any[] = [];
    for (let week of this.weeks) {
      for (let d of week) {
        result.push({
          date: this.formatDateLocal(d.dateObj),
          day: d.day,
          shift: null
        });
      }
    }
    return result;
  }
}
