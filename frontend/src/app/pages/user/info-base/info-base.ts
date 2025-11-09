import { NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { getdataRole } from '../../../services/pages/getPageRole.service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-info-base',
  imports: [NgIf, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './info-base.html',
  styleUrl: './info-base.scss',
})
export class InfoBase implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  hasChildRoute = false;
  role: string = "";
  name: string = "";
  onActivate() {
    this.hasChildRoute = true;
  }

  onDeactivate() {
    this.hasChildRoute = false;
  }
  async ngOnInit() {
    this.role = this.cookie.get("role");
    if (this.name == "") {
      const data = await getdataRole(this.role);
      this.name = data.fullname;
      this.cdr.detectChanges();
    }
  }

}
