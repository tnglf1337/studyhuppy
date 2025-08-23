import { Routes } from '@angular/router';
import {ModuleComponent} from './modul-service/module/module.component';
import {AddModuleComponent} from './modul-service/add-module/add-module.component';
import {OptionsComponent} from './modul-service/options/options.component';
import {StatisticsComponent} from './modul-service/statistics/statistics.component';
import {ModulServiceComponent} from './modul-service/modul-service.component';
import {LoginServiceComponent} from './auth-service/login-service/login-service.component';
import {AppLayoutComponent} from './app-layout/app-layout.component';
import {authenticationGuard} from './guard/authentication.guard';
import {RegisterServiceComponent} from './auth-service/register-service/register-service.component';
import {PwServiceComponent} from './auth-service/pw-service/pw-service.component';
import {UserProfileComponent} from './user-profile/user-profile.component';
import {GeneralComponent} from './modul-service/statistics/general/general.component';
import {ChartsComponent} from './modul-service/statistics/charts/charts.component';
import {KarteiServiceComponent} from './kartei-service/kartei-service.component';
import {LernenComponent} from './kartei-service/lernen/lernen.component';
import {StapelErstellenComponent} from './kartei-service/stapel-erstellen/stapel-erstellen.component';
import {StapelDetailsComponent} from './kartei-service/stapel-details/stapel-details.component';
import {ModulDetailsComponent} from './modul-service/module/modul-details/modul-details.component';
import {TermineDetailsComponent} from './modul-service/module/termine-details/termine-details.component';
import {AdminServiceComponent} from './auth-service/admin-service/admin-service.component';
import {authorityGuard} from './guard/authority.guard';
import {UnauthorizedComponent} from './auth-service/unauthorized/unauthorized.component';
import { AgbComponent } from './app-layout/footer/agb/agb.component'
import { ImpressumComponent } from './app-layout/footer/impressum/impressum.component'
import { DsgvoComponent } from './app-layout/footer/dsgvo/dsgvo.component'
import {
  MetricServiceComponent
} from './auth-service/admin-service/actuator-service/metric-service/metric-service.component';
import {MindmapServiceComponent} from './mindmap-service/mindmap-service.component';
import {MindmapDetailsComponent} from './mindmap-service/mindmap-details/mindmap-details.component';
import {HelpComponent} from './app-layout/header/help/help.component';
import {KontaktComponent} from './app-layout/footer/kontakt/kontakt.component';
import {SessionComponent} from './modul-service/session/session.component';
import {SessionStartComponent} from './modul-service/session/start/start.component';
import {SessionCreateComponent} from './modul-service/session/create/create.component';
import {SessionDeleteComponent} from './modul-service/session/delete/delete.component';

export const routes: Routes = [
  {
    path: 'register',
    component: RegisterServiceComponent
  },
  {
    path: 'login',
    component: LoginServiceComponent
  },
  {
    path: 'reset-pw',
    component: PwServiceComponent
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: 'agb',
    component: AgbComponent
  },
  {
    path: 'impressum',
    component: ImpressumComponent
  },
  {
    path: 'datenschutz',
    component: DsgvoComponent
  },
  {
    path: 'kontakt',
    component: KontaktComponent
  },
  {
    path: "hilfe",
    component: HelpComponent
  },
  {
    path: '',
    component: AppLayoutComponent,
    children:
      [
        {
          path: 'profil',
          component: UserProfileComponent,
          canActivate: [authenticationGuard]
        },
        {
          path: 'admin',
          component: AdminServiceComponent,
          canActivate: [authenticationGuard, authorityGuard]
        },
        {
          path: 'metrics/:service',
          component: MetricServiceComponent,
          canActivate: [authenticationGuard, authorityGuard]
        },
        {
        path: 'module',
        component: ModulServiceComponent,
        canActivate: [authenticationGuard],
        children:
          [
            { path: 'meine-module',
              component: ModuleComponent,
            },
            {
              path: 'modul-details/:fachId',
              component: ModulDetailsComponent
            },
            {
              path: 'termine-details',
              component: TermineDetailsComponent
            },
            { path: 'add-modul',
              component: AddModuleComponent,
            },
            {
              path: 'session',
              component: SessionComponent,
            },
            {
              path: 'session/start',
              component: SessionStartComponent
            },
            {
              path: 'session/create',
              component: SessionCreateComponent
            },
            {
              path: 'session/delete',
              component: SessionDeleteComponent
            },
            {
              path: 'einstellungen',
              component: OptionsComponent,
              canActivate: [authenticationGuard]
            },
            {
              path: 'statistiken',
              component: StatisticsComponent,
              canActivate: [authenticationGuard],
              children:
                [
                  {
                    path: 'general',
                    component: GeneralComponent
                  },
                  {
                    path: 'charts',
                    component: ChartsComponent
                  }
                ]
            }
          ]
      },
        {
          path: 'kartei',
          component: KarteiServiceComponent,
          canActivate: [authenticationGuard]
        },
        {
          path: 'lernen/:fachId',
          component: LernenComponent,
          canActivate: [authenticationGuard]
        },
        {
          path: 'neuer-stapel',
          component: StapelErstellenComponent,
          canActivate: [authenticationGuard]
        },
        {
          path: 'stapel-details/:fachId',
          component: StapelDetailsComponent,
          canActivate: [authenticationGuard]
        },
        {
          path: 'mindmap',
          component: MindmapServiceComponent,
          canActivate: [authenticationGuard],
          children: [
            {
              path: "map-details/:modulId",
              component: MindmapDetailsComponent
            }
          ]
        }]
  },
  { path: '**', redirectTo: 'login' }
];
