import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MenuComponent } from '../list/menu.component';
import { MenuDetailComponent } from '../detail/menu-detail.component';
import { MenuUpdateComponent } from '../update/menu-update.component';
import { MenuRoutingResolveService } from './menu-routing-resolve.service';

const menuRoute: Routes = [
  {
    path: '',
    component: MenuComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MenuDetailComponent,
    resolve: {
      menu: MenuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MenuUpdateComponent,
    resolve: {
      menu: MenuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MenuUpdateComponent,
    resolve: {
      menu: MenuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(menuRoute)],
  exports: [RouterModule],
})
export class MenuRoutingModule {}
