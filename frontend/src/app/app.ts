import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Loading } from './pages/shared/loading/loading';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Fe_luanvan');
}
