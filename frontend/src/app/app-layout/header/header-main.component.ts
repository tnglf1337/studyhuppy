import {Component, inject} from '@angular/core';
import {NgIf} from '@angular/common';
import  { LoginStatusService } from '../../auth-service/login-service/login-status.service'
import {AvatarComponent} from '../../user-profile/avatar/avatar.component';
import {LogoComponent} from '../logo/logo.component';

@Component({
  selector: 'app-header-main',
  imports: [NgIf, AvatarComponent, LogoComponent],
  templateUrl: './header-main.component.html',
  standalone: true,
  styleUrls: ['./header-main.component.scss', '../../general.scss', '../../color.scss', '../../links.scss', '../../button.scss', '../../debug.scss']
})
export class HeaderMainComponent {
  loginStatusService = inject(LoginStatusService)
}
