import { NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [NgIf],
  templateUrl: './loading.html',
  styleUrl: './loading.scss',
})
export class Loading {
  @Input() isLoading: boolean = false;
}
