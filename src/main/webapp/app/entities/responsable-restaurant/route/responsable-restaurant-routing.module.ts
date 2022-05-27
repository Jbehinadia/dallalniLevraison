import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ResponsableRestaurantComponent } from '../list/responsable-restaurant.component';
import { ResponsableRestaurantDetailComponent } from '../detail/responsable-restaurant-detail.component';
import { ResponsableRestaurantUpdateComponent } from '../update/responsable-restaurant-update.component';
import { ResponsableRestaurantRoutingResolveService } from './responsable-restaurant-routing-resolve.service';

const responsableRestaurantRoute: Routes = [
  {
    path: '',
    component: ResponsableRestaurantComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ResponsableRestaurantDetailComponent,
    resolve: {
      responsableRestaurant: ResponsableRestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ResponsableRestaurantUpdateComponent,
    resolve: {
      responsableRestaurant: ResponsableRestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ResponsableRestaurantUpdateComponent,
    resolve: {
      responsableRestaurant: ResponsableRestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(responsableRestaurantRoute)],
  exports: [RouterModule],
})
export class ResponsableRestaurantRoutingModule {}
